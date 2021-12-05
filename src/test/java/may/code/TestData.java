package may.code;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.TestedUserStatus;
import may.code.api.exeptions.NotFoundException;
import may.code.api.store.entities.*;
import may.code.api.store.repositories.*;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class TestData {

    SchoolRepository schoolRepository;

    SchoolClassRepository schoolClassRepository;

    TestedUserRepository testedUserRepository;

    PsychologistRepository psychologistRepository;

    TokenRepository tokenRepository;

    private static final String PSYCHOLOGIST_LOGIN = "a.alexandrov";
    private static final String PSYCHOLOGIST_PASSWORD = "a.alexandrov";

    private static final String SCHOOL_NAME = "МОУ СОШ №1";
    private static final String SCHOOL_CLASS_NAME = "11Б";

    public SchoolClassEntity getSchoolClass() {
        return schoolClassRepository
                .findByName(SCHOOL_CLASS_NAME)
                .orElseThrow(() -> new NotFoundException("Class not found."));
    }

    public TestedUserEntity getTestedUser() {
        return testedUserRepository.getById(1);
    }

    public PsychologistEntity getPsychologist() {
        return psychologistRepository
                .findTopByLoginAndPassword(PSYCHOLOGIST_LOGIN, PSYCHOLOGIST_PASSWORD)
                .orElseThrow(() -> new NotFoundException("Psychologist doesn't exist"));
    }

    public String getPsychologistToken() {

        PsychologistEntity psychologist = getPsychologist();

        TokenEntity tokenEntity = tokenRepository
                .findByPsychologistId(psychologist.getId())
                .orElseGet(() ->
                        TokenEntity.builder()
                                .psychologist(psychologist)
                                .build()
                );

        tokenEntity.updateExpiredAt();

        tokenEntity = tokenRepository.saveAndFlush(tokenEntity);

        return tokenEntity.getToken();
    }

    @EventListener
    public void init(ApplicationStartedEvent event) {

        SchoolEntity school = schoolRepository
                .findByName(SCHOOL_NAME)
                .orElseGet(() ->
                        schoolRepository.saveAndFlush(
                                SchoolEntity.builder()
                                        .name(SCHOOL_NAME)
                                        .build()
                        )
                );

        SchoolClassEntity schoolClass = schoolClassRepository
                .findByName(SCHOOL_CLASS_NAME)
                .orElseGet(() ->
                        schoolClassRepository.saveAndFlush(
                                SchoolClassEntity.builder()
                                        .name(SCHOOL_CLASS_NAME)
                                        .school(school)
                                        .build()
                        )
                );

        psychologistRepository
                .findTopByLoginAndPassword(PSYCHOLOGIST_LOGIN, PSYCHOLOGIST_PASSWORD)
                .orElseGet(() ->
                        psychologistRepository
                                .saveAndFlush(
                                        PsychologistEntity.builder()
                                                .fio("Александров Алексей Александрович")
                                                .login(PSYCHOLOGIST_LOGIN)
                                                .password(PSYCHOLOGIST_PASSWORD)
                                                .schoolClass(schoolClass)
                                                .build()
                                )
                );
    }
}
