package may.code.api.store.repositories;

import lombok.NonNull;
import may.code.api.store.entities.TestedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TestedUserRepository extends JpaRepository<TestedUserEntity, Integer> {

    @Query("SELECT u FROM TestedUserEntity u " +
            "WHERE :isFiltered = FALSE " +
            "OR (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%'))) " +
            "ORDER BY u.lastName, u.firstName")
    List<TestedUserEntity> findAllByFilter(boolean isFiltered, String filter);

    @Query("SELECT u FROM TestedUserEntity u " +
            "WHERE u.schoolClass.id =:classId " +
            "AND (:isFiltered = FALSE " +
                "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :filter, '%'))" +
                "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%'))" +
            ") " +
            "ORDER BY u.lastName, u.firstName")
    List<TestedUserEntity> findAllByFilterAndClass(boolean isFiltered, String filter, Integer classId);

    @Query("SELECT u FROM TestedUserEntity u WHERE u.id =:userId AND u.schoolClass.id =:schoolClassId")
    Optional<TestedUserEntity> findByIdAndSchoolClassId(Integer userId, Integer schoolClassId);

    Optional<TestedUserEntity> findTopByLoginAndPassword(@NonNull String login, @NonNull String password);
}
