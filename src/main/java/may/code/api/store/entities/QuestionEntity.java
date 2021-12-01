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
@Table(name = "question")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    Short questionOrder;

    @Column(length = 256)
    String text;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    TestEntity test;

    @Column(name = "test_id", updatable = false, insertable = false)
    Long testId;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    List<AnswerEntity> answers = new ArrayList<>();

    public static QuestionEntity makeDefault() {
        return builder().build();
    }
}
