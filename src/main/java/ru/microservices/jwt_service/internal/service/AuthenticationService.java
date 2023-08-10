package ru.microservices.jwt_service.internal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.microservices.jwt_service.internal.RouteGuideUsers;
import ru.microservices.jwt_service.internal.entity.ETokenType;
import ru.microservices.jwt_service.internal.entity.Token;
import ru.microservices.jwt_service.internal.payload.TokenDTO;
import ru.microservices.jwt_service.internal.payload.UserDTO;
import ru.microservices.jwt_service.internal.repository.TokenRepository;
import ru.microservices.jwt_service.util.JwtTools;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final TokenRepository tokenRepository;
    private final RouteGuideUsers routeGuideUsers;


    private void saveUserAccessToken(String jwtToken, Long userId) { // saving token
        var token = Token.builder()
                .token(jwtToken)
                .userId(userId)
                .tokenType(ETokenType.ACCESS)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void saveUserRefreshToken(String jwtToken, Long userId) { // saving token
        var token = Token.builder()
                .token(jwtToken)
                .userId(userId)
                .tokenType(ETokenType.REFRESH)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Long id) {
        var validUserTokens = tokenRepository
                .findAllByIsExpiredAndIsRevokedAndUserId(
                        false,
                        false,
                        id
                );
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setIsExpired(true);
            token.setIsRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Transactional
    public void signUp(String username, String password) {
        routeGuideUsers.addUser(username, password);
    }

    public TokenDTO signIn(String username, String password) {

        var isValid = routeGuideUsers.validateUser(username, password);

        if (!isValid) {
            throw new IllegalArgumentException("Login and password don't matches");
        }

        var user = routeGuideUsers.getUserByUsername(username);

        var accessToken = JwtTools.generateToken(user);
        var refreshToken = JwtTools.generateRefreshToken(user);
        revokeAllUserTokens(user.getId());

        saveUserAccessToken(accessToken, user.getId());
        saveUserRefreshToken(refreshToken, user.getId());

        return new TokenDTO(accessToken, refreshToken);
    }

    public void logOut() {

        Jwt jwtToken = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();

        log.info(jwtToken.getClaims().toString());
        Long id = (Long) jwtToken.getClaims().get("userId");

        revokeAllUserTokens(id);
    }

    public TokenDTO refreshToken(String refreshToken) {
        var user = getUserByToken(refreshToken);

        if (JwtTools.isTokenValid(refreshToken, user) && checkTokenType(refreshToken, ETokenType.REFRESH)) {
            var accessToken = JwtTools.generateToken(user); // generate new
            revokeAllUserTokens(user.getId());
            saveUserAccessToken(accessToken, user.getId()); // save new
            return new TokenDTO(accessToken, refreshToken);
        }
        throw new IllegalArgumentException("Illegal token");
        //проверить тип токена
    }

    public boolean validateToken(String token) {
        var user = getUserByToken(token);

        return JwtTools.isTokenValid(token, user);
    }

    private UserDTO getUserByToken(String token) {
        var username = JwtTools.extractUsername(token);

        if (username == null)
            throw new IllegalArgumentException("Token validation failed: Username is null");

        return routeGuideUsers.getUserByUsername(username);
    }

    public boolean checkTokenType(String token, ETokenType tokenType) {
        return tokenRepository.existsTokenByTokenAndTokenType(
                token,
                tokenType
        );
    }

}