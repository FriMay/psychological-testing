package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.UserRole;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    @NonNull
    Long id;

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
    UserRole role;

    @NonNull
    Long schoolClassId;
}
