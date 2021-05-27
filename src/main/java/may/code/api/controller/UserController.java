package may.code.api.controller;

import com.ibm.icu.text.Transliterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.UserRole;
import may.code.api.dto.AckDTO;
import may.code.api.dto.UserDTO;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.UserDTOFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.UserEntity;
import may.code.api.store.repositories.SchoolClassRepository;
import may.code.api.store.repositories.UserRepository;
import may.code.api.utils.StringChecker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@ExtensionMethod(StringChecker.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class UserController {

    UserRepository userRepository;

    SchoolClassRepository schoolClassRepository;

    UserDTOFactory userDTOFactory;

    ControllerAuthenticationService authenticationService;

    public static final String FETCH_USERS = "/api/schools/classes/users";
    public static final String FETCH_USERS_BY_CLASS = "/api/classes/{classId}/users";
    public static final String CREATE_USER = "/api/schools/classes/{classId}/users";
    public static final String DELETE_USER = "/api/schools/classes/users/{userId}";
    public static final String GET_USER_ID_BY_LOGIN_AND_PASSWORD = "/api/schools/classes/users/id";

    private static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    private static final Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);

    private static final int PASSWORD_LENGTH = 10;

    @GetMapping(FETCH_USERS_BY_CLASS)
    public ResponseEntity<List<UserDTO>> fetchUsersByClass(
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long classId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        boolean isFiltered = !filter.trim().isEmpty();

        List<UserEntity> users = userRepository.findAllByFilterAndClass(isFiltered, filter, classId);

        return ResponseEntity.ok(userDTOFactory.createUserDTOList(users));
    }

    @GetMapping(FETCH_USERS)
    public ResponseEntity<List<UserDTO>> fetchUsers(
            @RequestParam(defaultValue = "") String filter,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        boolean isFiltered = !filter.trim().isEmpty();

        List<UserEntity> users = userRepository.findAllByFilter(isFiltered, filter);

        return ResponseEntity.ok(userDTOFactory.createUserDTOList(users));
    }

    @PostMapping(CREATE_USER)
    public ResponseEntity<UserDTO> createUser(
            @RequestParam Instant birthday,
            @RequestParam String firstName,
            @RequestParam(defaultValue = "") String middleName,
            @RequestParam String lastName,
            @RequestParam UserRole userRole,
            @PathVariable Long classId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        firstName = firstName.trim();
        lastName = lastName.trim();

        firstName.checkOnEmpty("firstName");
        lastName.checkOnEmpty("lastName");
        middleName = middleName.trim().isEmpty() ? null : middleName;

        String login = makeLogin(firstName, lastName);
        String password = makePassword();

        SchoolClassEntity schoolClass = schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Класс с идентификатором \"%s\" не найден.", classId))
                );

        UserEntity user = userRepository.saveAndFlush(
                UserEntity.makeDefault(
                        firstName,
                        middleName,
                        lastName,
                        login,
                        password,
                        birthday,
                        userRole,
                        schoolClass
                )
        );

        return ResponseEntity.ok(userDTOFactory.createUserDTO(user));
    }

    @DeleteMapping(DELETE_USER)
    public ResponseEntity<AckDTO> deleteUser(
            @PathVariable Long userId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }

        return ResponseEntity.ok(AckDTO.makeDefault(true));
    }

    @GetMapping(GET_USER_ID_BY_LOGIN_AND_PASSWORD)
    public ResponseEntity<Long> getUserIdByLoginAndPassword(
            @RequestParam String login,
            @RequestParam String password) {

        UserEntity user = userRepository
                .findTopByLoginAndPassword(login, password)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким логином и паролем не существует."));

        return ResponseEntity.ok(user.getId());
    }

    private String makeLogin(String firstName, String lastName) {

        String firstNameTransliterated = toLatinTrans.transliterate(firstName.toLowerCase());

        String lastNameTransliterated = toLatinTrans.transliterate(lastName.toLowerCase());

        return String.format("%s.%s", firstNameTransliterated.charAt(0), lastNameTransliterated);
    }

    private String makePassword() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, PASSWORD_LENGTH);
    }
}
