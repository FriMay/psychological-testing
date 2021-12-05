package may.code.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import may.code.BaseTest;
import may.code.TestData;
import may.code.api.controller.PsychologistController;
import may.code.api.controller.TestController;
import may.code.api.controller.TestedUserController;
import may.code.api.domains.TestedUserStatus;
import may.code.api.dto.*;
import may.code.api.store.repositories.TestRepository;
import may.code.api.store.repositories.TestedUserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = {
        TestedUserController.class,
        TestController.class,
        PsychologistController.class,
})
class PsychologistTests extends BaseTest {

    @Autowired
    TestData testData;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TestedUserRepository testedUserRepository;

    @Autowired
    TestRepository testRepository;

    private static TestedUserDto testedUser;

    private static TestDto test;

    @BeforeAll
    public void init() throws Exception {

        // Init test user
        {
            byte[] answer = mvc.perform(
                    MockMvcRequestBuilders
                            .post(TestedUserController.CREATE_USER)
                            .param("birthday", Instant.now().toString())
                            .param("firstName", "Владислав")
                            .param("lastName", "Бирюков")
                            .param("middleName", "Евгеньевич")
                            .param("testedUserStatus", TestedUserStatus.STUDENT.name())
                            .header("token", testData.getPsychologistToken())
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsByteArray();

            testedUser = mapper.readValue(answer, TestedUserDto.class);
        }

        // Init test
        {
            List<AnswerDto> defaultAnswers = Arrays.asList(
                    AnswerDto.builder()
                            .text("First")
                            .order((short) 1)
                            .build(),
                    AnswerDto.builder()
                            .text("Second")
                            .order((short) 2)
                            .build(),
                    AnswerDto.builder()
                            .text("Third")
                            .order((short) 3)
                            .build()
            );

            test = TestDto.builder()
                    .name("Simple test")
                    .questions(
                            Arrays.asList(
                                    QuestionDto.builder()
                                            .text("First question.")
                                            .order((short) 1)
                                            .answers(defaultAnswers)
                                            .build(),
                                    QuestionDto.builder()
                                            .text("Second question.")
                                            .order((short) 2)
                                            .answers(defaultAnswers)
                                            .build(),
                                    QuestionDto.builder()
                                            .text("Third question.")
                                            .order((short) 3)
                                            .answers(defaultAnswers)
                                            .build()
                            )
                    )
                    .build();

            byte[] answer = mvc.perform(
                    MockMvcRequestBuilders
                            .post(TestController.CREATE_OR_UPDATE_TEST)
                            .content(mapper.writeValueAsBytes(test))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("token", testData.getPsychologistToken())
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsByteArray();

            test = mapper.readValue(answer, TestDto.class);

            List<PersonTemplateDto> personTemplates = Arrays.asList(
                    PersonTemplateDto.builder()
                            .text("Здоровый ученик")
                            .userShouldAnswers(
                                    Arrays.asList(
                                            UserShouldAnswerDto.builder()
                                                    .answerId(test.getQuestions().get(0).getAnswers().get(1).getId())
                                                    .questionId(test.getQuestions().get(0).getId())
                                                    .build(),
                                            UserShouldAnswerDto.builder()
                                                    .answerId(test.getQuestions().get(1).getAnswers().get(2).getId())
                                                    .questionId(test.getQuestions().get(1).getId())
                                                    .build()
                                    )
                            )
                            .build(),
                    PersonTemplateDto.builder()
                            .text("Не здоровый ученик")
                            .userShouldAnswers(
                                    Arrays.asList(
                                            UserShouldAnswerDto.builder()
                                                    .answerId(test.getQuestions().get(0).getAnswers().get(2).getId())
                                                    .questionId(test.getQuestions().get(0).getId())
                                                    .build(),
                                            UserShouldAnswerDto.builder()
                                                    .answerId(test.getQuestions().get(1).getAnswers().get(0).getId())
                                                    .questionId(test.getQuestions().get(1).getId())
                                                    .build()
                                    )
                            )
                            .build()
            );

            answer = mvc.perform(
                    MockMvcRequestBuilders
                            .post(TestController.UPDATE_PERSON_TEMPLATES, test.getId())
                            .content(mapper.writeValueAsBytes(personTemplates))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("token", testData.getPsychologistToken())
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsByteArray();

            test = mapper.readValue(answer, TestDto.class);
        }

    }

    @Test
    void should_PassSuccessfully_When_CompleteTest() throws Exception {

        List<TestedUserAnswerDto> testedUserAnswers = Arrays.asList(
                TestedUserAnswerDto.builder()
                        .questionId(test.getQuestions().get(0).getId())
                        .answerId(test.getQuestions().get(0).getAnswers().get(0).getId())
                        .createdAt(Instant.now())
                        .build(),
                TestedUserAnswerDto.builder()
                        .questionId(test.getQuestions().get(1).getId())
                        .answerId(test.getQuestions().get(1).getAnswers().get(0).getId())
                        .createdAt(Instant.now())
                        .build(),
                TestedUserAnswerDto.builder()
                        .questionId(test.getQuestions().get(2).getId())
                        .answerId(test.getQuestions().get(2).getAnswers().get(1).getId())
                        .createdAt(Instant.now())
                        .build()
        );

        mvc.perform(
                MockMvcRequestBuilders
                        .post(TestController.COMPLETE_TEST, testedUser.getId(), test.getId())
                        .param("startAt", Instant.now().toString())
                        .content(mapper.writeValueAsBytes(testedUserAnswers))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(
                MockMvcRequestBuilders
                        .get(PsychologistController.GET_TEST_ANSWERS, test.getId())
                        .header("token", testData.getPsychologistToken())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
    }

    @AfterAll
    public void clear() {
        testedUserRepository.deleteById(testedUser.getId());
        testRepository.deleteById(test.getId());
    }
}
