package may.code.api.controller;

import com.ibm.icu.text.Transliterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import may.code.api.domains.TestedUserStatus;
import may.code.api.dto.TestedUserDto;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestedUserDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.PsychologistEntity;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.TestedUserEntity;
import may.code.api.store.repositories.TestedUserRepository;
import may.code.api.utils.StringChecker;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@ExtensionMethod(StringChecker.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@Transactional
public class TestedUserController {

    TestedUserRepository testedUserRepository;

    TestedUserDtoFactory testedUserDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String AUTHORIZE = "/api/tested-users/authorize";
    public static final String GET_SCHOOL_CLASS_TESTED_USERS = "/api/tested-users";
    public static final String CREATE_USER = "/api/tested-users";

    private static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    private static final Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);

    private static final int PASSWORD_LENGTH = 10;

    @GetMapping(AUTHORIZE)
    public Integer authorize(
            @RequestParam String login,
            @RequestParam String password) {

        return testedUserRepository
                .findTopByLoginAndPassword(login, password)
                .map(TestedUserEntity::getId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким логином и паролем не существует."));
    }

    @GetMapping(GET_SCHOOL_CLASS_TESTED_USERS)
    public List<TestedUserDto> fetchUsers(
            @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        SchoolClassEntity schoolClass = psychologist.getSchoolClass();

        if (Objects.isNull(schoolClass)) {
            throw new BadRequestException("Вы не привязаны ни к одному из классов.");
        }

        List<TestedUserEntity> users = schoolClass.getTestedUsers();

        return testedUserDtoFactory.createTestedUserDtoList(users);
    }

    @PostMapping(CREATE_USER)
    public TestedUserDto createUser(
            @RequestParam Instant birthday,
            @RequestParam String firstName,
            @RequestParam(defaultValue = "") String middleName,
            @RequestParam String lastName,
            @RequestParam TestedUserStatus testedUserStatus,
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

        return testedUserDtoFactory.createTestedUserDto(user);
    }

    public static String makeLogin(String firstName, String lastName) {

        String firstNameTransliterated = toLatinTrans.transliterate(firstName.toLowerCase());

        String lastNameTransliterated = toLatinTrans.transliterate(lastName.toLowerCase());

        return String.format("%s.%s", firstNameTransliterated.charAt(0), lastNameTransliterated);
    }

    public static String makePassword() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, PASSWORD_LENGTH);
    }
}
