package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AckDTO {

    Boolean answer;

    public static AckDTO makeDefault(Boolean answer) {
        return builder()
                .answer(answer)
                .build();
    }
}
