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
@Table(name = "psychologist")
public class PsychologistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(length = 300)
    @NonNull
    String fio;

    @Column(length = 64)
    @NonNull
    String login;

    @Column(length = 64)
    @NonNull
    String password;

    @ManyToOne
    @JoinColumn(name = "school_class_id", referencedColumnName = "id")
    SchoolClassEntity schoolClass;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "psychologist_id", referencedColumnName = "id")
    List<TestEntity> tests = new ArrayList<>();
}
