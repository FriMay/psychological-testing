package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.TestedUserAnswer;
import may.code.api.store.entities.SampleTestEntity;
import may.code.api.store.repositories.SampleTestRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Transactional
public class SampleTestController {

    SampleTestRepository sampleTestRepository;

    @GetMapping("/api/sample/test")
    public String help() {

        List<TestedUserAnswer> testedUserAnswers = Arrays.asList(
                TestedUserAnswer.builder()
                        .answer_id(1)
                        .question_id(2)
                        .build(),
                TestedUserAnswer.builder()
                        .answer_id(1)
                        .question_id(1)
                        .build(),
                TestedUserAnswer.builder()
                        .answer_id(2)
                        .question_id(3)
                        .build()
        );

        // Поговнокодим
        sampleTestRepository
                .saveAndFlush(
                        SampleTestEntity.builder()
                                .testedUserAnswers(testedUserAnswers)
                                .build()
                );

        return "Ok, i help you bro";
    }

    @GetMapping("/api/get/get")
    public List<SampleTestEntity> get() {
        //Поговнокодим ещё больше
        return sampleTestRepository.findAll();
    }
}
