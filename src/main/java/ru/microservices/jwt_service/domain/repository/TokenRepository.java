package ru.microservices.jwt_service.domain.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import ru.microservices.jwt_service.domain.entity.ETokenType;
import ru.microservices.jwt_service.domain.entity.Token;

import java.util.List;

public interface TokenRepository extends JpaRepositoryImplementation<Token, Long> {
    List<Token> findAllByIsExpiredAndIsRevokedAndUserId(Boolean isExpired, Boolean isRevoked, Long userId);

    Boolean existsTokenByTokenAndTokenType(String token, ETokenType tokenType);

    List<Token> findAllByIsExpiredAndIsRevoked(Boolean isExpired, Boolean isRevoked);
}
