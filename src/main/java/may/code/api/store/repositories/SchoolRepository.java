package may.code.api.store.repositories;

import lombok.NonNull;
import may.code.api.store.entities.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Integer> {
    Optional<SchoolEntity> findByName(String name);
}
