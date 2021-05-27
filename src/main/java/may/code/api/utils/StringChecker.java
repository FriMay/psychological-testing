package may.code.api.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import may.code.api.exeptions.BadRequestException;

@UtilityClass
public class StringChecker {

    public void checkOnEmpty(
            @NonNull String value,
            @NonNull String fieldName) {

        if (value.trim().isEmpty()) {
            throw new BadRequestException(String.format("Поле с названием \"%s\" не может быть пустым!", fieldName));
        }
    }
}
