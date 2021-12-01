package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.tested_user.TestedUserAnswerDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonTemplateDto {

    String text;

    List<TestedUserAnswerDto> testedUserAnswers;
}
