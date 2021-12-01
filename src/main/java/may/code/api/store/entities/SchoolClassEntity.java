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
@Table(name = "school_class")
public class SchoolClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(length = 8)
    @NonNull
    String name;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", referencedColumnName = "id")
    List<TestedUserEntity> testedUsers = new ArrayList<>();

    @NonNull
    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    SchoolEntity school;

    @Column(name = "school_id", updatable = false, insertable = false)
    Integer schoolId;

    public static SchoolClassEntity makeDefault(String name, SchoolEntity school) {
        return builder()
                .name(name)
                .school(school)
                .build();
    }
}
