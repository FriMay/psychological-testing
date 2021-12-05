package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonAnalyzeDto {

    Integer personTemplateId;

    Integer testedUserAnswers;

    Integer totalAnswers;
}
