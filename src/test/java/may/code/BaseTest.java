package may.code;

import may.code.api.exeptions.CustomErrorController;
import may.code.api.factory.TestAnswerDtoFactory;
import may.code.api.factory.TestDtoFactory;
import may.code.api.factory.TestedUserDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.services.TestService;
import may.code.configs.WebConfig;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

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
        TestService.class
})
@AutoConfigureMockMvc
@AutoConfigureDataJpa
public abstract class BaseTest {

}
