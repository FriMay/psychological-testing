package may.code;

import lombok.extern.log4j.Log4j2;
import may.code.api.exeptions.CustomErrorController;
import may.code.api.factory.TestAnswerDtoFactory;
import may.code.api.factory.TestDtoFactory;
import may.code.api.factory.TestedUserDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.configs.WebConfig;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

@RunWith(JUnitPlatform.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({

        //Configs
        WebConfig.class,

        //Custom controllers
        CustomErrorController.class,

        //Factories
        TestDtoFactory.class,
        TestedUserDtoFactory.class,
        TestAnswerDtoFactory.class,

        //Services
        ControllerAuthenticationService.class,
        TestData.class,
})
@AutoConfigureMockMvc
@AutoConfigureDataJpa
public abstract class BaseTest {

}
