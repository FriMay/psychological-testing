package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.TestUserDTO;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestUserDTOFactory;
import may.code.store.entities.TestUserEntity;
import may.code.store.repositories.PsychologistRepository;
import may.code.store.repositories.SchoolClassRepository;
import may.code.store.repositories.TestRepository;
import may.code.store.repositories.TestUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class PsychologistController {

    TestRepository testRepository;

    TestUserRepository testUserRepository;

    SchoolClassRepository schoolClassRepository;

    PsychologistRepository psychologistRepository;

    TestUserDTOFactory testUserDTOFactory;

    public static final String GET_TEST_RESULTS = "/api/psychologists/{psychologistId}/tests/{testId}/results";
    public static final String GENERATE_LINK_FOR_TEST = "/api/psychologists/{psychologistId}/schools/classes/{classId}/tests/{testId}/generate-link";

    public static final String LINK_TEMPLATE = "/test/%s/class/%s/psychologist/%s";

    @GetMapping(GET_TEST_RESULTS)
    public ResponseEntity<List<TestUserDTO>> getTestResults(
            @PathVariable Long psychologistId,
            @PathVariable Long testId) {

        checkTestById(testId);

        checkPsychologistById(psychologistId);

        List<TestUserEntity> testUserList = testUserRepository.findAllByTestIdAndPsychologistId(testId, psychologistId);

        return ResponseEntity.ok(testUserDTOFactory.createTestUserDTOList(testUserList));
    }

    @GetMapping(GENERATE_LINK_FOR_TEST)
    public ResponseEntity<String> generateLinkForTest(
            @PathVariable Long psychologistId,
            @PathVariable Long classId,
            @PathVariable Long testId) {

        checkTestById(testId);

        checkPsychologistById(psychologistId);

        schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Класс с идентификатором \"%s\" не найден.", classId))
                );

        return ResponseEntity.ok(String.format(LINK_TEMPLATE, testId, classId, psychologistId));
    }

    private void checkTestById(Long testId) {
        testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );
    }

    private void checkPsychologistById(Long psychologistId) {
        psychologistRepository
                .findById(psychologistId)
                .orElseThrow(() ->
                        new NotFoundException
                                (String.format("Психолог с идентификатором \"%s\" не найден.", psychologistId)
                                )
                );
    }
}
