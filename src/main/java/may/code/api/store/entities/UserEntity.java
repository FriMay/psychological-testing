package may.code.api.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.UserRole;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NonNull
    String firstName;

    @NonNull
    String lastName;

    String middleName;

    @NonNull
    Instant birthday;

    @NonNull
    @Enumerated(EnumType.STRING)
    UserRole role;

    @NonNull
    String login;

    @NonNull
    String password;

    @NonNull
    @ManyToOne
    SchoolClassEntity schoolClass;

    public static UserEntity makeDefault(
            String firstName,
            String middleName,
            String lastName,
            String login,
            String password,
            Instant birthday,
            UserRole role,
            SchoolClassEntity schoolClass) {
        return builder()
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .login(login)
                .password(password)
                .birthday(birthday)
                .role(role)
                .schoolClass(schoolClass)
                .build();
    }
}
