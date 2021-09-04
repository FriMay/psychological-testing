package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.AckDto;
import may.code.api.dto.AnswerDto;
import may.code.api.dto.QuestionDto;
import may.code.api.dto.TestDto;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.*;
import may.code.api.store.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class TestController {

    TestRepository testRepository;

    UserRepository userRepository;

    TestUserRepository testUserRepository;

    SchoolClassRepository schoolClassRepository;

    PsychologistRepository psychologistRepository;

    TestDtoFactory testDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String FETCH_TESTS = "/api/tests";
    public static final String GET_TEST = "/api/tests/{testId}";
    public static final String CREATE_OR_UPDATE_TEST = "/api/tests";
    public static final String DELETE_TEST = "/api/tests/{testId}";
    public static final String COMPLETE_TEST = "/api/schools/classes/{classId}/users/{userId}/tests/{testId}/psychologists/{psychologistId}/compete";

    @GetMapping(FETCH_TESTS)
    public ResponseEntity<List<TestDto>> fetchTests(@RequestParam(defaultValue = "") String filter) {

        boolean isFiltered = !filter.trim().isEmpty();

        List<TestEntity> tests = testRepository.findAllByFilter(isFiltered, filter);

        return ResponseEntity.ok(testDtoFactory.createTestDtoList(tests));
    }

    @GetMapping(GET_TEST)
    public ResponseEntity<TestDto> getTest(@PathVariable Long testId) {

        TestEntity test = testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );

        return ResponseEntity.ok(testDtoFactory.createTestDto(test));
    }

    @PostMapping(CREATE_OR_UPDATE_TEST)
    public ResponseEntity<TestDto> createOrUpdateTest(
            @RequestBody TestDto test,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        TestEntity testEntity = convertTestToEntity(test);

        testEntity = testRepository.saveAndFlush(testEntity);

        return ResponseEntity.ok(testDtoFactory.createTestDto(testEntity));
    }

    @DeleteMapping(DELETE_TEST)
    public ResponseEntity<AckDto> deleteTest(
            @PathVariable Long testId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        testRepository
                .findById(testId)
                .ifPresent(test -> {

                    test.getQuestions().forEach(it -> it.getAnswers().clear());
                    test.getQuestions().clear();

                    test = testRepository.saveAndFlush(test);

                    testRepository.delete(test);
                });

        return ResponseEntity.ok(AckDto.makeDefault(true));
    }

    @PostMapping(COMPLETE_TEST)
    public ResponseEntity<AckDto> completeTest(
            @PathVariable Long classId,
            @PathVariable Long testId,
            @PathVariable Long userId,
            @PathVariable Long psychologistId,
            @RequestParam String answers) {

        TestEntity test = getTestOrThrowNotFound(testId);

        List<String> answerList = Arrays.stream(answers.split(","))
                .filter(it -> !it.trim().isEmpty())
                .collect(Collectors.toList());

        if (answerList.size() != test.getQuestions().size()) {
            throw new BadRequestException("Вы ответили не на все вопросы.");
        }

        schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Класс с идентификатором \"%s\" не найден.", classId))
                );

        UserEntity user = userRepository
                .findByIdAndSchoolClassId(userId, classId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователь с идентификатором \"%s\" не найден.", userId))
                );

        PsychologistEntity psychologist = psychologistRepository
                .findById(psychologistId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Психолог с индентификатором \"%s\" не найден.", psychologistId))
                );

        testUserRepository.saveAndFlush(
                TestUserEntity.builder()
                        .answers(answers)
                        .user(user)
                        .test(test)
                        .psychologist(psychologist)
                        .build()
        );

        return ResponseEntity.ok(AckDto.makeDefault(true));
    }

    private TestEntity getTestOrThrowNotFound(Long testId) {
        return testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );
    }

    private TestEntity convertTestToEntity(TestDto dto) {

        Long testId = dto.getId();

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
        answer.setName(dto.getName());
        answer.setAnswerOrder(dto.getOrder());

        return answer;
    }
}
