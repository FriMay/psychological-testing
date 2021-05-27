package may.code.api.store.repositories;

import may.code.api.store.entities.SchoolClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClassEntity, Long> {
    void deleteByIdAndSchoolId(Long schoolClassId, Long schoolId);
}
