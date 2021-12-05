package may.code.api;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import may.code.BaseTest;
import may.code.TestData;
import may.code.api.controller.TestedUserController;
import may.code.api.domains.TestedUserStatus;
import may.code.api.store.entities.TestedUserEntity;
import may.code.api.store.repositories.TestedUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(TestedUserController.class)
@Import(TestData.class)
class TestedUserTests extends BaseTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    TestData testData;

    @Autowired
    TestedUserRepository testedUserRepository;

    @Test
    void helloWorld() {

        testedUserRepository.saveAndFlush(
                TestedUserEntity.builder()
                        .id(1)
                        .birthday(Instant.now())
                        .firstName("Алексей")
                        .lastName("Уваров")
                        .status(TestedUserStatus.STUDENT)
                        .login("a.yvarov")
                        .password("helloworld")
                        .schoolClass(testData.getSchoolClass())
                        .build()
        );
    }
}
