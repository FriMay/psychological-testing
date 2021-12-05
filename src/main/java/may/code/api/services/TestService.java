package may.code.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.IPersonTemplateAnalyze;
import may.code.api.dto.PersonAnalyzeDto;
import may.code.api.exeptions.AccessDeniedException;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.store.entities.PsychologistEntity;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.TestEntity;
import may.code.api.store.repositories.TestAnswerRepository;
import may.code.api.store.repositories.TestRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestService {

    TestRepository testRepository;

    TestAnswerRepository testAnswerRepository;

    public static final String LINK_TEMPLATE = "/test/%s/class/%s";

    public Map<Integer, List<PersonAnalyzeDto>> getTestAnswerIdToPersonAnalyze(
            SchoolClassEntity schoolClass,
            TestEntity test) {

        List<IPersonTemplateAnalyze> personAnalyzes = testAnswerRepository
                .analyzePersonTemplates(schoolClass.getId(), test.getId());

        Map<Integer, List<PersonAnalyzeDto>> testAnswerIdToPersonAnalyze = new HashMap<>();

        personAnalyzes
                .forEach(personAnalyze ->
                        testAnswerIdToPersonAnalyze
                                .computeIfAbsent(personAnalyze.getTestAnswerId(), key -> new ArrayList<>())
                                .add(PersonAnalyzeDto.builder()
                                        .testedUserAnswers(personAnalyze.getTestedUserAnswers())
                                        .totalAnswers(personAnalyze.getTotalAnswers())
                                        .personTemplateId(personAnalyze.getPersonTemplateId())
                                        .build()
                                )
                );
        return testAnswerIdToPersonAnalyze;
    }

    public String generateLinkForTest(PsychologistEntity psychologist, Integer testId) {

        getTestOrThrowException(psychologist, testId);

        SchoolClassEntity schoolClass = psychologist.getSchoolClass();

        if (Objects.isNull(schoolClass)) {

            throw new BadRequestException(
                    "Вы не можете сгенерировать ссылку, так как не привязаны ни к одному классу."
            );
        }

        return String.format(LINK_TEMPLATE, testId, schoolClass.getId());
    }

    public TestEntity getTestOrThrowException(PsychologistEntity psychologist, Integer testId) {

        TestEntity test = testRepository
                .findById(testId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Тест с идентификатором \"%s\" не найден.", testId))
                );

        if (!Objects.equals(test.getPsychologist().getId(), psychologist.getId())) {
            throw new AccessDeniedException("Вы не можете получить доступ к этому тесту.");
        }

        return test;
    }
}

