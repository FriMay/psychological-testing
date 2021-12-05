package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestAnswerDto {

    LiteTestDto test;

    List<TestedUserAnswerDto> answers;

    List<PersonAnalyzeDto> personAnalyzes;

    TestedUserDto user;
}
