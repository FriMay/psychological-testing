package may.code.api.store.repositories;

import may.code.api.store.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, Long> {

    @Query("SELECT t FROM TestEntity t " +
            "WHERE :isFiltered = FALSE " +
                "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :filter, '%'))" +
            "ORDER BY t.name")
    List<TestEntity> findAllByFilter(boolean isFiltered, String filter);
}
