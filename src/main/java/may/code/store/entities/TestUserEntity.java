package may.code.store.entities;

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
    String answers;

    @Builder.Default
    @NonNull
    Instant createdAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "psychologist_id", referencedColumnName = "id")
    PsychologistEntity psychologist;

    @Column(name = "psychologist_id", insertable = false, updatable = false)
    Long psychologistId;
}
