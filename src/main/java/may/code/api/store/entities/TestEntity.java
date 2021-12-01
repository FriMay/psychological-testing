package may.code.api.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "test")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(length = 256)
    String name;

    @Builder.Default
    Boolean status = false;

    @ManyToOne
    @JoinColumn(name = "psychologist_id", referencedColumnName = "id")
    PsychologistEntity psychologist;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    List<TestAnswerEntity> testAnswers = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    List<QuestionEntity> questions = new ArrayList<>();

    public static TestEntity makeDefault() {
        return builder().build();
    }
}
