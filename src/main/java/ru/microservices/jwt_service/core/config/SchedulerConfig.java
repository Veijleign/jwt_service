package ru.microservices.jwt_service.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.microservices.jwt_service.domain.entity.Token;
import ru.microservices.jwt_service.domain.repository.TokenRepository;

import java.util.List;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final TokenRepository tokenRepository;

    //    @Scheduled(initialDelay = 5000, fixedRate = @Value("${jwt.delete-time}"))
    @Scheduled(initialDelay = 5000, fixedRate = 10000)
    public void deleteOldTokens() {
        List<Token> expiredAndRevokedTokens = tokenRepository
                .findAllByIsExpiredAndIsRevoked(
                        true,
                        true
                );

        tokenRepository.deleteAllInBatch(expiredAndRevokedTokens);

        expiredAndRevokedTokens.forEach(token -> log.info("Token '" + token + "' was deleted.")
        );
    }
}
