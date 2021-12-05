package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.TestedUserStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestedUserDto {

    @NonNull
    Integer id;

    @NonNull
    String firstName;

    String middleName;

    @NonNull
    String lastName;

    @NonNull
    String login;

    @NonNull
    String password;

    @NonNull
    Instant birthday;

    @NonNull
    TestedUserStatus role;

    @NonNull
    Integer schoolClassId;
}
