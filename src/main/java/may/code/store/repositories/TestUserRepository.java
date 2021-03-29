package may.code.store.repositories;

import may.code.store.entities.TestUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestUserRepository extends JpaRepository<TestUserEntity, Long> {

    @Query("SELECT tu FROM TestUserEntity tu WHERE tu.test.id =:testId AND tu.psychologist.id =:psychologistId")
    List<TestUserEntity> findAllByTestIdAndPsychologistId(Long testId, Long psychologistId);
}
