package may.code.api.domains;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestedUserAnswer implements Serializable {

    Integer question_id;

    Integer answer_id;

    Instant created_at;
}
