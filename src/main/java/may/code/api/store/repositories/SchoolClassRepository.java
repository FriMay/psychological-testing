package may.code.api.store.repositories;

import lombok.NonNull;
import may.code.api.store.entities.SchoolClassEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClassEntity, Integer> {
    Optional<SchoolClassEntity> findByName(String name);
}
