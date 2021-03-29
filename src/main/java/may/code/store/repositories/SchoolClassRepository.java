package may.code.store.repositories;

import may.code.store.entities.SchoolClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClassEntity, Long> {
    void deleteByIdAndSchoolId(Long schoolClassId, Long schoolId);
}
