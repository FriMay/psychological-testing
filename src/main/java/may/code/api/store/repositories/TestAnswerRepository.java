package may.code.api.store.repositories;

import may.code.api.store.entities.TestAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestAnswerRepository extends JpaRepository<TestAnswerEntity, Integer> {

}
