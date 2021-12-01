package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.TestUserDto;
import may.code.api.dto.tested_user.TestedUserDto;
import may.code.api.exeptions.AccessDeniedException;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestUserDtoFactory;
import may.code.api.factory.TestedUserDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.PsychologistEntity;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.TestEntity;
import may.code.api.store.repositories.TestRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@Transactional
public class PsychologistController {

    TestRepository testRepository;

    TestedUserDtoFactory testedUserDtoFactory;

    TestUserDtoFactory testUserDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String GET_TEST_RESULTS = "/api/psychologists/tests/{testId}/results";
    public static final String GET_SCHOOL_CLASS_TESTED_USERS = "/api/psychologists/tested_users";
    public static final String GENERATE_LINK_FOR_TEST = "/api/psychologists/tests/{testId}/generate-link";

    public static final String LINK_TEMPLATE = "/test/%s/class/%s";

    @GetMapping(GET_TEST_RESULTS)
    public List<TestUserDto> getTestResults(@PathVariable Integer testId,
                                            @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        TestEntity test = getTestOrThrowException(testId);

        checkTestByPsychologistAndTest(psychologist, test);

        return testUserDtoFactory.createTestUserDtoList(test.getTestAnswers());
    }

    @GetMapping(GENERATE_LINK_FOR_TEST)
    public String generateLinkForTest(@PathVariable Integer testId, @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        TestEntity test = getTestOrThrowException(testId);

        checkTestByPsychologistAndTest(psychologist, test);

        SchoolClassEntity schoolClass = psychologist.getSchoolClass();

        if (Objects.isNull(schoolClass)) {

            throw new BadRequestException(
                    "Вы не можете сгенерировать ссылку, так как не привязаны ни к одному классу."
            );
        }

        return String.format(LINK_TEMPLATE, testId, schoolClass.getId());
    }

    @GetMapping(GET_SCHOOL_CLASS_TESTED_USERS)
    public List<TestedUserDto> getSchoolClassTestedUsers(@RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        if (Objects.isNull(psychologist.getSchoolClass())) {
            throw new BadRequestException("Вы не привязаны ни к одному из классов.");
        }

        return testedUserDtoFactory.createTestedUserDtoList(psychologist.getSchoolClass().getTestedUsers());
    }

    private void checkTestByPsychologistAndTest(PsychologistEntity psychologist, TestEntity test) {

        if (!Objects.equals(test.getPsychologist().getId(), psychologist.getId())) {
            throw new AccessDeniedException("Вы не можете сгенерировать ссылку на чужой тест.");
        }
    }

    private TestEntity getTestOrThrowException(Integer testId) {

        return testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );
    }
}
