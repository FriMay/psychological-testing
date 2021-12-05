package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.IPersonTemplateAnalyze;
import may.code.api.dto.AckDto;
import may.code.api.dto.PersonAnalyzeDto;
import may.code.api.dto.TestAnswerDto;
import may.code.api.exeptions.AccessDeniedException;
import may.code.api.exeptions.BadRequestException;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.TestAnswerDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.services.TestService;
import may.code.api.store.entities.PsychologistEntity;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.TestEntity;
import may.code.api.store.repositories.TestAnswerRepository;
import may.code.api.store.repositories.TestRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@Transactional
public class PsychologistController {

    TestService testService;

    TestAnswerDtoFactory testAnswerDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String GET_TEST_ANSWERS = "/api/psychologists/tests/{testId}/results";
    public static final String GENERATE_LINK_FOR_TEST = "/api/psychologists/tests/{testId}/generate-link";


    @GetMapping(GET_TEST_ANSWERS)
    public List<TestAnswerDto> getTestAnswers(@PathVariable Integer testId,
                                              @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        SchoolClassEntity schoolClass = psychologist.getSchoolClass();

        if (Objects.isNull(schoolClass)) {
            throw new BadRequestException("Вы не привязаны ни к одному классу.");
        }

        TestEntity test = testService.getTestOrThrowException(psychologist, testId);

        Map<Integer, List<PersonAnalyzeDto>> testAnswerIdToPersonAnalyze = testService
                .getTestAnswerIdToPersonAnalyze(schoolClass, test);

        return testAnswerDtoFactory.createTestAnswerDtoList(test.getTestAnswers(), testAnswerIdToPersonAnalyze);
    }

    @GetMapping(GENERATE_LINK_FOR_TEST)
    public String generateLinkForTest(@PathVariable Integer testId, @RequestHeader(defaultValue = "") String token) {

        PsychologistEntity psychologist = authenticationService.authenticate(token);

        return testService.generateLinkForTest(psychologist, testId);
    }
}
