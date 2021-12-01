package may.code.api.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "answer")
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    Short answerOrder;

    @Column(length = 50)
    String text;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    QuestionEntity question;

    @Column(name = "question_id", updatable = false, insertable = false)
    Long questionId;

    public static AnswerEntity makeDefault() {
        return builder().build();
    }
}
