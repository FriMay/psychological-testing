package may.code.api.controller;

import jdk.nashorn.internal.parser.Token;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.exeptions.NotFoundException;
import may.code.api.store.entities.PsychologistEntity;
import may.code.api.store.entities.TokenEntity;
import may.code.api.store.repositories.PsychologistRepository;
import may.code.api.store.repositories.TokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class AuthorizationController {

    TokenRepository tokenRepository;

    PsychologistRepository psychologistRepository;

    public static final String AUTHORIZE = "/api/psychologists/authorizations";

    @GetMapping(AUTHORIZE)
    public ResponseEntity<String> authorize(
            @RequestParam String login,
            @RequestParam String password) {

        psychologistRepository
                .findTopByLoginAndPassword(login, password)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким логином и паролем не найден."));

        TokenEntity tokenEntity = tokenRepository.saveAndFlush(TokenEntity.builder().build());

        return ResponseEntity.ok(tokenEntity.getToken());
    }
}
