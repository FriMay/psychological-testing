package may.code.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "school_class")
public class SchoolClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NonNull
    String name;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    SchoolEntity school;

    @Column(name = "school_id", updatable = false, insertable = false)
    Long schoolId;

    public static SchoolClassEntity makeDefault(String name, SchoolEntity school) {
        return builder()
                .name(name)
                .school(school)
                .build();
    }
}
