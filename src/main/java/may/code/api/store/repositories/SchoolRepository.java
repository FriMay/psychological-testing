package may.code.api.store.repositories;

import lombok.NonNull;
import may.code.api.store.entities.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Long> {

    Boolean existsByName(@NonNull String name);

    @Query("SELECT s FROM SchoolEntity s " +
            "WHERE :isFiltered = FALSE " +
            "OR LOWER(s.name) LIKE LOWER(CONCAT('%',:filter, '%')) " +
            "ORDER BY s.name DESC")
    List<SchoolEntity> findAllByFilter(boolean isFiltered, String filter);
}
