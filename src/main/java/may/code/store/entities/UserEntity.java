package may.code.store.entities;

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
    String fullName;

    @NonNull
    Instant birthday;

    @NonNull
    @Enumerated(EnumType.STRING)
    UserRole role;

    @NonNull
    @ManyToOne
    SchoolClassEntity schoolClass;

    public static UserEntity makeDefault(
            String fullName,
            Instant birthday,
            UserRole role,
            SchoolClassEntity schoolClass) {
        return builder()
                .fullName(fullName)
                .birthday(birthday)
                .role(role)
                .schoolClass(schoolClass)
                .build();
    }
}
