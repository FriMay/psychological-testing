package may.code.api.store.repositories;

import may.code.api.store.entities.TestUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestUserRepository extends JpaRepository<TestUserEntity, Long> {

    @Query("SELECT tu FROM TestUserEntity tu WHERE tu.test.id =:testId")
    List<TestUserEntity> findAllByTestIdAndPsychologistId(Long testId);
}
