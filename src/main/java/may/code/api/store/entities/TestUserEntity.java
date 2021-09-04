package may.code.api.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "test_users")
public class TestUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NonNull
    @ManyToOne
    UserEntity user;

    @NonNull
    @ManyToOne
    TestEntity test;

    @NonNull
    @ManyToOne
    PsychologistEntity psychologist;

    @Column(length = 10485760)
    @NonNull
    String answers;

    @Builder.Default
    @NonNull
    Instant createdAt = Instant.now();
}
