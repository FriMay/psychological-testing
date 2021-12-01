package may.code.api.store.repositories;

import lombok.NonNull;
import may.code.api.store.entities.PsychologistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PsychologistRepository extends JpaRepository<PsychologistEntity, Integer> {
    Optional<PsychologistEntity> findTopByLoginAndPassword(@NonNull String login, @NonNull String password);
}
