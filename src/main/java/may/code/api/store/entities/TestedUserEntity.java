package may.code.api.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.TestedUserStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tested_user")
public class TestedUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(length = 64)
    @NonNull
    String firstName;

    @Column(length = 64)
    @NonNull
    String lastName;

    @Column(length = 64)
    String middleName;

    @NonNull
    Instant birthday;

    @Column(length = 64)
    @NonNull
    @Enumerated(EnumType.STRING)
    TestedUserStatus status;

    @Column(length = 128)
    @NonNull
    String login;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "tested_user_id", referencedColumnName = "id")
    List<TestAnswerEntity> testedUserAnswers = new ArrayList<>();

    @Column(length = 10)
    @NonNull
    String password;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "school_class_id", referencedColumnName = "id")
    SchoolClassEntity schoolClass;

    public static TestedUserEntity makeDefault(
            String firstName,
            String middleName,
            String lastName,
            String login,
            String password,
            Instant birthday,
            TestedUserStatus status,
            SchoolClassEntity schoolClass) {
        return builder()
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .login(login)
                .password(password)
                .birthday(birthday)
                .status(status)
                .schoolClass(schoolClass)
                .build();
    }
}
