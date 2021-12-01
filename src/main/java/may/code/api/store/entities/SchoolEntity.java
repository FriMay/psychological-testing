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
@Table(name = "school")
public class SchoolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(length = 128)
    @NonNull
    String name;

    @Builder.Default
    @OneToMany
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    List<SchoolClassEntity> schoolClasses = new ArrayList<>();

    public static SchoolEntity makeDefault(String schoolName) {

        return builder()
                .name(schoolName)
                .build();
    }
}
