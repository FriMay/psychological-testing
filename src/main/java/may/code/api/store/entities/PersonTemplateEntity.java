package may.code.api.store.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.UserShouldAnswer;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "person_template")
//https://ligerlearn.com/creating-java-jpa-entities-with-a-json-field/
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PersonTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(length = 1000)
    String text;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", length = 20000)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    List<UserShouldAnswer> answers;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    TestEntity test;
}
