package may.code.api.store.repositories;

import may.code.api.domains.IPersonTemplateAnalyze;
import may.code.api.store.entities.TestAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestAnswerRepository extends JpaRepository<TestAnswerEntity, Integer> {

    @Query(value =
            "SELECT pt.id AS personTemplateId, " +
                    "TOTAL_COUNT(pt.id) AS totalAnswers, " +
                    "INTERSECTIONS_COUNT(pt.answers, ta.answers) AS testedUserAnswers, " +
                    "ta.id AS testAnswerId " +
                    "FROM test_answer ta " +
                        "INNER JOIN tested_user tu ON ta.tested_user_id = tu.id " +
                        "INNER JOIN school_class sc ON tu.school_class_id = :schoolClassId " +
                        "INNER JOIN test t ON ta.test_id = :testId " +
                        "INNER JOIN person_template pt ON t.id = pt.test_id " +
                        "GROUP BY ta.id, pt.id",
            nativeQuery = true
    )
    List<IPersonTemplateAnalyze> analyzePersonTemplates(Integer schoolClassId, Integer testId);
}
