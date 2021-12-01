package may.code.api.store.repositories;

import may.code.api.store.entities.SampleTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleTestRepository extends JpaRepository<SampleTestEntity, Integer> {
}
