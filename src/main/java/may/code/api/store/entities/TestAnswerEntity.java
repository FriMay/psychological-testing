package may.code.api.store.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.TestedUserAnswer;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "test_answer")
//https://ligerlearn.com/creating-java-jpa-entities-with-a-json-field/
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class TestAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @NonNull
    @ManyToOne
    TestedUserEntity testedUser;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    TestEntity test;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", length = 20000)
    List<TestedUserAnswer> answers;

    @Builder.Default
    @NonNull
    Instant createdAt = Instant.now();
}
