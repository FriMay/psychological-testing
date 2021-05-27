package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.TestUserDTO;
import may.code.api.dto.UserDTO;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestUserDTOFactory;
import may.code.api.factory.UserDTOFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.TestUserEntity;
import may.code.api.store.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class PsychologistController {

    UserRepository userRepository;

    TestRepository testRepository;

    TestUserRepository testUserRepository;

    SchoolClassRepository schoolClassRepository;

    UserDTOFactory userDTOFactory;

    TestUserDTOFactory testUserDTOFactory;

    ControllerAuthenticationService authenticationService;

    public static final String GET_TEST_RESULTS = "/api/psychologists/tests/{testId}/results";
    public static final String GET_USERS_BY_CLASS = "/api/psychologists/schools/classes/{classId}";
    public static final String GENERATE_LINK_FOR_TEST = "/api/psychologists/schools/classes/{classId}/tests/{testId}/generate-link";

    public static final String LINK_TEMPLATE = "/test/%s/class/%s";

    @GetMapping(GET_TEST_RESULTS)
    public ResponseEntity<List<TestUserDTO>> getTestResults(
            @PathVariable Long testId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        checkTestById(testId);

        List<TestUserEntity> testUserList = testUserRepository.findAllByTestIdAndPsychologistId(testId);

        return ResponseEntity.ok(testUserDTOFactory.createTestUserDTOList(testUserList));
    }

    @GetMapping(GENERATE_LINK_FOR_TEST)
    public ResponseEntity<String> generateLinkForTest(
            @PathVariable Long classId,
            @PathVariable Long testId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        checkTestById(testId);

        checkClassById(classId);

        return ResponseEntity.ok(String.format(LINK_TEMPLATE, testId, classId));
    }

    @GetMapping(GET_USERS_BY_CLASS)
    public ResponseEntity<List<UserDTO>> getUsersByClass(
            @PathVariable Long classId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        checkClassById(classId);

        List<UserDTO> users = userDTOFactory.createUserDTOList(
                userRepository.findAllByFilterAndClass(false, "", classId)
        );

        return ResponseEntity.ok(users);
    }

    private void checkClassById(Long classId) {
        schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Класс с идентификатором \"%s\" не найден.", classId))
                );
    }

    private void checkTestById(Long testId) {
        testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );
    }
}
