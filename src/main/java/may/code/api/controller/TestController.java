package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.AckDTO;
import may.code.api.dto.AnswerDTO;
import may.code.api.dto.QuestionDTO;
import may.code.api.dto.TestDTO;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestDTOFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.*;
import may.code.api.store.repositories.SchoolClassRepository;
import may.code.api.store.repositories.TestRepository;
import may.code.api.store.repositories.TestUserRepository;
import may.code.api.store.repositories.UserRepository;
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

    TestDTOFactory testDTOFactory;

    ControllerAuthenticationService authenticationService;

    public static final String FETCH_TESTS = "/api/tests";
    public static final String GET_TEST = "/api/tests/{testId}";
    public static final String CREATE_OR_UPDATE_TEST = "/api/tests";
    public static final String DELETE_TEST = "/api/tests/{testId}";
    public static final String COMPLETE_TEST = "/api/schools/classes/{classId}/users/{userId}/tests/{testId}/psychologists/{psychologistId}/compete";

    @GetMapping(FETCH_TESTS)
    public ResponseEntity<List<TestDTO>> fetchTests(@RequestParam(defaultValue = "") String filter) {

        boolean isFiltered = !filter.trim().isEmpty();

        List<TestEntity> tests = testRepository.findAllByFilter(isFiltered, filter);

        return ResponseEntity.ok(testDTOFactory.createTestDTOList(tests));
    }

    @GetMapping(GET_TEST)
    public ResponseEntity<TestDTO> getTest(@PathVariable Long testId) {

        TestEntity test = testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );

        return ResponseEntity.ok(testDTOFactory.createTestDTO(test));
    }

    @PostMapping(CREATE_OR_UPDATE_TEST)
    public ResponseEntity<TestDTO> createOrUpdateTest(
            @RequestBody TestDTO test,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        TestEntity testEntity = convertTestToEntity(test);

        testEntity = testRepository.saveAndFlush(testEntity);

        return ResponseEntity.ok(testDTOFactory.createTestDTO(testEntity));
    }

    @DeleteMapping(DELETE_TEST)
    public ResponseEntity<AckDTO> deleteTest(
            @PathVariable Long testId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        TestEntity test = testRepository
                .findById(testId)
                .orElse(null);

        if (test != null) {

            test.getQuestions().forEach(it -> it.getAnswers().clear());
            test.getQuestions().clear();

            test = testRepository.saveAndFlush(test);

            testRepository.delete(test);
        }

        return ResponseEntity.ok(AckDTO.makeDefault(true));
    }

    @PostMapping(COMPLETE_TEST)
    public ResponseEntity<AckDTO> completeTest(
            @PathVariable Long classId,
            @PathVariable Long testId,
            @PathVariable Long userId,
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

        testUserRepository.saveAndFlush(
                TestUserEntity.builder()
                        .answers(answers)
                        .user(user)
                        .test(test)
                        .build()
        );

        return ResponseEntity.ok(AckDTO.makeDefault(true));
    }

    private TestEntity getTestOrThrowNotFound(Long testId) {
        return testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );
    }

    private TestEntity convertTestToEntity(TestDTO dto) {

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

    private QuestionEntity convertQuestionToEntity(QuestionDTO dto) {

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

    private AnswerEntity convertAnswerToEntity(AnswerDTO dto) {

        AnswerEntity answer = AnswerEntity.makeDefault();

        answer.setId(dto.getId());
        answer.setName(dto.getName());
        answer.setAnswerOrder(dto.getOrder());

        return answer;
    }
}
