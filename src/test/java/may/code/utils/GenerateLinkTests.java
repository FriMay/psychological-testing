package may.code.utils;

import lombok.extern.log4j.Log4j2;
import may.code.api.services.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Log4j2
class GenerateLinkTests {

    @ParameterizedTest
    @MethodSource("correctArguments")
    void should_PassSuccessfully_When_PassCorrectArguments(Integer testId, Integer schoolClassId) {

        log.info(String.format("testId=%s; schoolClassId=%s", testId, schoolClassId));

        try {

            String link = TestService.generateLink(testId, schoolClassId);

            log.info(String.format("Ссылка успешно создана: \"%s\"", link));

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    private static Stream<Arguments> correctArguments() {
        return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(2, 5),
                Arguments.of(33, 241)
        );
    }
}
