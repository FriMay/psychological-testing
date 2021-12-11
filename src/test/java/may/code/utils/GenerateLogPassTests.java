package may.code.utils;

import lombok.extern.log4j.Log4j2;
import may.code.api.controller.TestedUserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Log4j2
class GenerateLogPassTests {

    @ParameterizedTest
    @MethodSource("correctArguments")
    void should_PassSuccessfully_When_PassCorrectArguments(String firstName, String lastName) {

        log.info(String.format("firstName=%s; lastName=%s", firstName, lastName));

        try {

            String login = TestedUserController.makeLogin(firstName, lastName);
            String password = TestedUserController.makePassword();

            log.info("Логин и пароль успешно создан!");
            log.info(String.format("Логин: %s", login));
            log.info(String.format("Пароль: %s", password));

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    private static Stream<Arguments> correctArguments() {
        return Stream.of(
                Arguments.of("Алексей", "Кондаков"),
                Arguments.of("Владислав", "Бирюков"),
                Arguments.of("Андрей", "Дибин")
        );
    }
}
