package may.code.api.dto.tested_user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestedUserAnswerDto {

    Integer questionId;

    Integer answerId;

    Instant createdAt;
}
