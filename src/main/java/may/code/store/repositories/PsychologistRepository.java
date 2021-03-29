package may.code.store.repositories;

import may.code.store.entities.PsychologistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PsychologistRepository extends JpaRepository<PsychologistEntity, Long> {
}
