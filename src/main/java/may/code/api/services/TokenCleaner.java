package may.code.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.store.repositories.TokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
@Transactional
public class TokenCleaner {

    TokenRepository tokenRepository;

    @Scheduled(cron = "0 * * * * *")
    public void clean() {
        tokenRepository.deleteAllByExpiredAtBefore(Instant.now());
    }

}
