package may.code.api.controller;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.AckDto;
import may.code.api.dto.AnswerDto;
import may.code.api.dto.QuestionDto;
import may.code.api.dto.TestDto;
import may.code.api.dto.tested_user.TestedUserAnswerDto;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.*;
import may.code.api.store.repositories.*;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@Transactional
public class TestController {

    TestRepository testRepository;

    TestedUserRepository testedUserRepository;

    TestAnswerRepository testAnswerRepository;

    SchoolClassRepository schoolClassRepository;

    TestDtoFactory testDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String FETCH_TESTS = "/api/tests";
    public static final String GET_TEST = "/api/tests/{testId}";
    public static final String CREATE_OR_UPDATE_TEST = "/api/tests";
    public static final String DELETE_TEST = "/api/tests/{testId}";
    public static final String COMPLETE_TEST = "/api/tested-users/{testedUserId}/tests/{testId}/compete";

    @GetMapping(FETCH_TESTS)
    public List<TestDto> fetchTests(@RequestParam(defaultValue = "") String filter,
                                    @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        boolean isFiltered = !filter.trim().isEmpty();

        List<TestEntity> tests = testRepository.findAllByFilter(isFiltered, filter);

        return testDtoFactory.createTestDtoList(tests);
    }

    @GetMapping(GET_TEST)
    public TestDto getTest(@PathVariable Integer testId) {

        TestEntity test = testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );

        return testDtoFactory.createTestDto(test);
    }

    //TODO: Modify logic
    @PostMapping(CREATE_OR_UPDATE_TEST)
    public TestDto createOrUpdateTest(@RequestBody TestDto test,
                                      @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        TestEntity testEntity = convertTestToEntity(test);

        testEntity = testRepository.saveAndFlush(testEntity);

        return testDtoFactory.createTestDto(testEntity);
    }

    @DeleteMapping(DELETE_TEST)
    public AckDto deleteTest(@PathVariable Integer testId,
                             @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        TestEntity test = psychologist
                .getTests()
                .stream()
                .filter(it -> Objects.equals(it.getId(), testId))
                .findAny()
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с \"%s\" идентификатором не найден.", testId))
                );

        test.getQuestions().forEach(it -> it.getAnswers().clear());
        test.getQuestions().clear();

        test = testRepository.saveAndFlush(test);

        testRepository.delete(test);

        return AckDto.makeDefault(true);
    }

    @PostMapping(COMPLETE_TEST)
    public AckDto completeTest(@PathVariable Integer testId,
                               @PathVariable Integer testedUserId,
                               @RequestBody TestedUserAnswersDto testedUserAnswersDto) {

        TestEntity test = getTestOrThrowException(testId);

        List<TestedUserAnswerDto> testedUserAnswers = testedUserAnswersDto.getTestedUserAnswers();

        if (testedUserAnswers.size() != test.getQuestions().size()) {
            throw new BadRequestException("Вы ответили не на все вопросы.");
        }

        checkAllAnswers(testedUserAnswers, test);

        TestedUserEntity user = testedUserRepository
                .findById(testedUserId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Пользователь с идентификатором \"%s\" не найден.", testedUserId)
                        )
                );

        testAnswerRepository.saveAndFlush(
                TestAnswerEntity.builder()
//                        .answers(answers)
                        .testedUser(user)
                        .test(test)
                        .build()
        );

        return AckDto.makeDefault(true);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class TestedUserAnswersDto {
        List<TestedUserAnswerDto> testedUserAnswers;
    }

    private void checkAllAnswers(List<TestedUserAnswerDto> testedUserAnswers, TestEntity test) {

        Map<Integer, Integer> questionIdToAnswerIdMap = new HashMap<>();

        testedUserAnswers
                .forEach(testedUserAnswer ->
                        questionIdToAnswerIdMap.put(testedUserAnswer.getQuestionId(), testedUserAnswer.getAnswerId())
                );

        test
                .getQuestions()
                .forEach(question -> {

                    //TODO: Check question contains answer, which user send
                    if (Objects.isNull(questionIdToAnswerIdMap.get(question.getId()))) {
                        throw new BadRequestException("Вы ответили не на все вопросы.");
                    }
                });
    }

    private TestEntity getTestOrThrowException(Integer testId) {
        return testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );
    }

    private TestEntity convertTestToEntity(TestDto dto) {

        Integer testId = dto.getId();

        TestEntity test;
        if (testId == null) {
            test = TestEntity.makeDefault();
        } else {
            test = testRepository
                    .findById(testId)
                    .orElseThrow(() ->
                            new NotFoundException(String.format("Тест с индентификатором \"%s\" не найден.", testId))
                    );
        }

        test.setName(dto.getName());
        test.getQuestions().clear();

        test.getQuestions().addAll(
                dto.getQuestions()
                        .stream()
                        .map(this::convertQuestionToEntity)
                        .collect(Collectors.toList())
        );

        return test;
    }

    private QuestionEntity convertQuestionToEntity(QuestionDto dto) {

        QuestionEntity question = QuestionEntity.makeDefault();

        question.setId(dto.getId());
        question.setQuestionOrder(dto.getOrder());
        question.setText(dto.getText());
        question.getAnswers().clear();

        question.getAnswers().addAll(
                dto.getAnswers()
                        .stream()
                        .map(this::convertAnswerToEntity)
                        .collect(Collectors.toList())
        );

        return question;
    }

    private AnswerEntity convertAnswerToEntity(AnswerDto dto) {

        AnswerEntity answer = AnswerEntity.makeDefault();

        answer.setId(dto.getId());
        answer.setText(dto.getText());
        answer.setAnswerOrder(dto.getOrder());

        return answer;
    }
}
