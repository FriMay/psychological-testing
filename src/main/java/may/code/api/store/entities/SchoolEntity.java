package may.code.api.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "school")
public class SchoolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(length = 10485760)
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
