package may.code.api.controller;

import com.ibm.icu.text.Transliterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import may.code.api.domains.TestedUserStatus;
import may.code.api.dto.tested_user.TestedUserDto;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestedUserDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.PsychologistEntity;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.TestedUserEntity;
import may.code.api.store.repositories.TestedUserRepository;
import may.code.api.utils.StringChecker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@ExtensionMethod(StringChecker.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class TestedUserController {

    TestedUserRepository testedUserRepository;

    public static final String AUTHORIZE = "/api/tested-users/authorize";

    @GetMapping(AUTHORIZE)
    public ResponseEntity<Integer> authorize(
            @RequestParam String login,
            @RequestParam String password) {

        TestedUserEntity user = testedUserRepository
                .findTopByLoginAndPassword(login, password)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким логином и паролем не существует."));

        return ResponseEntity.ok(user.getId());
    }

//    SchoolClassRepository schoolClassRepository;
//
    TestedUserDtoFactory testedUserDtoFactory;
//
    ControllerAuthenticationService authenticationService;

//    public static final String FETCH_USERS = "/api/schools/classes/users";
//    public static final String FETCH_USERS_BY_CLASS = "/api/classes/{classId}/users";
    public static final String CREATE_USER = "/api/schools/classes/{classId}/users";
//    public static final String DELETE_USER = "/api/schools/classes/users/{userId}";

    private static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";
//
    private static final Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
//
    private static final int PASSWORD_LENGTH = 10;

//    @GetMapping(FETCH_USERS_BY_CLASS)
//    public ResponseEntity<List<TestedUserDto>> fetchUsersByClass(
//            @RequestParam(defaultValue = "") String filter,
//            @PathVariable Integer classId,
//            @RequestHeader(defaultValue = "") String token) {
//
//        authenticationService.authenticate(token);
//
//        boolean isFiltered = !filter.trim().isEmpty();
//
//        List<TestedUserEntity> users = testedUserRepository.findAllByFilterAndClass(isFiltered, filter, classId);
//
//        return ResponseEntity.ok(testedUserDtoFactory.createTestedUserDtoList(users));
//    }
//
//    @GetMapping(FETCH_USERS)
//    public ResponseEntity<List<TestedUserDto>> fetchUsers(
//            @RequestParam(defaultValue = "") String filter,
//            @RequestHeader(defaultValue = "") String token) {
//
//        authenticationService.authenticate(token);
//
//        boolean isFiltered = !filter.trim().isEmpty();
//
//        List<TestedUserEntity> users = testedUserRepository.findAllByFilter(isFiltered, filter);
//
//        return ResponseEntity.ok(testedUserDtoFactory.createTestedUserDtoList(users));
//    }

    @PostMapping(CREATE_USER)
    public ResponseEntity<TestedUserDto> createUser(
            @RequestParam Instant birthday,
            @RequestParam String firstName,
            @RequestParam(defaultValue = "") String middleName,
            @RequestParam String lastName,
            @RequestParam TestedUserStatus testedUserStatus,
            @PathVariable Integer classId,
            @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        SchoolClassEntity schoolClass = psychologist.getSchoolClass();

        if (Objects.isNull(schoolClass)) {
            throw new BadRequestException(
                    "Вы не можете создать пользователя, " +
                            "так как не привязаны ни к одному из классов"
            );
        }

        firstName = firstName.trim();
        lastName = lastName.trim();

        firstName.checkOnEmpty("firstName");
        lastName.checkOnEmpty("lastName");
        middleName = middleName.trim().isEmpty() ? null : middleName;

        String login = makeLogin(firstName, lastName);
        String password = makePassword();

        TestedUserEntity user = testedUserRepository.saveAndFlush(
                TestedUserEntity.makeDefault(
                        firstName,
                        middleName,
                        lastName,
                        login,
                        password,
                        birthday,
                        testedUserStatus,
                        schoolClass
                )
        );

        return ResponseEntity.ok(testedUserDtoFactory.createTestedUserDto(user));
    }

//    @DeleteMapping(DELETE_USER)
//    public ResponseEntity<AckDto> deleteUser(
//            @PathVariable Integer userId,
//            @RequestHeader(defaultValue = "") String token) {
//
//        authenticationService.authenticate(token);
//
//        if (testedUserRepository.existsById(userId)) {
//            testedUserRepository.deleteById(userId);
//        }
//
//        return ResponseEntity.ok(AckDto.makeDefault(true));
//    }
//
    private String makeLogin(String firstName, String lastName) {

        String firstNameTransliterated = toLatinTrans.transliterate(firstName.toLowerCase());

        String lastNameTransliterated = toLatinTrans.transliterate(lastName.toLowerCase());

        return String.format("%s.%s", firstNameTransliterated.charAt(0), lastNameTransliterated);
    }

    private String makePassword() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, PASSWORD_LENGTH);
    }
}
