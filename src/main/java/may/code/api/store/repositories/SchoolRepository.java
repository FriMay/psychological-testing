package may.code.api.store.repositories;

import may.code.api.store.entities.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Integer> {
    Optional<SchoolEntity> findByName(String name);
}
