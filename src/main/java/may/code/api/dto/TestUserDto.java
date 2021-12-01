package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.tested_user.TestedUserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestUserDto {

    LiteTestDto test;

    String answers;

    TestedUserDto user;
}
