package may.code.api.store.repositories;

import may.code.api.store.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {

    void deleteAllByExpiredAtBefore(Instant currentDate);

    Optional<TokenEntity> findByPsychologistId(Integer psychologistId);
}
