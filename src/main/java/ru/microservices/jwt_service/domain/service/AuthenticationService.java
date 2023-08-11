package ru.microservices.jwt_service.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.microservices.jwt_service.core.config.user_service.UserService;
import ru.microservices.jwt_service.core.exception.ExtendedError;
import ru.microservices.jwt_service.core.exception.ExtendedException;
import ru.microservices.jwt_service.domain.entity.ETokenType;
import ru.microservices.jwt_service.domain.entity.Token;
import ru.microservices.jwt_service.domain.mapper.UserMapper;
import ru.microservices.jwt_service.domain.payload.TokenDTO;
import ru.microservices.jwt_service.domain.payload.UserDTO;
import ru.microservices.jwt_service.domain.repository.TokenRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional
    public void signUp(String username, String password) {
        userService.userServiceBlockingStub()
                .create(
                        userMapper.toCreateUserRequest(
                                username,
                                password
                        )
                );
        log.info("User with username: " + username + "was created");
    }

    public TokenDTO signIn(String username, String password) {

        Boolean isValid = userMapper.fromValidateUserResponse(
                userService.userServiceBlockingStub()
                        .validateUser(
                                userMapper.toValidateUserRequest(
                                        username,
                                        password
                                )
                        )
        );

        if (!isValid) {
            throw ExtendedException.of(ExtendedError.WRONG_CREDENTIALS);
        }

        UserDTO user = userMapper.toDTO(
                userService.userServiceBlockingStub()
                        .getUserByUsername(
                                userMapper.toUserUsernameRequest(
                                        username
                                )
                        )
        );


        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user.getId());
        saveUserAccessToken(accessToken, user.getId());
        saveUserRefreshToken(refreshToken, user.getId());

        return new TokenDTO(accessToken, refreshToken);
    }

    public void logout() {

        Jwt jwtToken = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();

        Long userId = (Long) jwtToken.getClaims().get("userId");

        revokeAllUserTokens(userId);

        log.info("User with userID: " + userId + "logout");
    }

    public TokenDTO refreshToken(String refreshToken) {
        var user = getUserByToken(refreshToken);

        if (jwtService.isTokenValid(refreshToken, user)
                && checkTokenType(refreshToken, ETokenType.REFRESH)
        ) {
            String accessToken = jwtService.generateToken(user);
            revokeAllUserTokens(user.getId());
            saveUserAccessToken(accessToken, user.getId());

            return new TokenDTO(
                    accessToken,
                    refreshToken
            );
        }
        throw ExtendedException.of(ExtendedError.ILLEGAL_TOKEN);
    }

    public boolean validateToken(String token) {
        UserDTO user = getUserByToken(token);

        return jwtService.isTokenValid(
                token,
                user
        );
    }

    private UserDTO getUserByToken(String token) {
        String username = jwtService.extractUsername(token);

        if (username == null)
            throw ExtendedException.of(ExtendedError.UNABLE_TO_EXTRACT_USERNAME_FROM_TOKEN);

        return userMapper.toDTO(
                userService.userServiceBlockingStub()
                        .getUserByUsername(
                                userMapper.toUserUsernameRequest(
                                        username
                                )
                        )
        );
    }

    public Boolean checkTokenType(String token, ETokenType tokenType) {
        return tokenRepository.existsTokenByTokenAndTokenType(
                token,
                tokenType
        );
    }


    private void saveUserAccessToken(
            String jwtToken,
            Long userId
    ) { // saving token
        Token token = Token.builder()
                .token(jwtToken)
                .userId(userId)
                .tokenType(ETokenType.ACCESS)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
        log.info("Access token for userId: " + userId + "was saved");
    }

    private void saveUserRefreshToken(
            String jwtToken,
            Long userId
    ) { // saving token
        Token token = Token.builder()
                .token(jwtToken)
                .userId(userId)
                .tokenType(ETokenType.REFRESH)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
        log.info("Refresh token for userId: " + userId + "was saved");
    }

    private void revokeAllUserTokens(Long userId) {
        List<Token> validUserTokens = tokenRepository
                .findAllByIsExpiredAndIsRevokedAndUserId(
                        false,
                        false,
                        userId
                );

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setIsExpired(true);
            token.setIsRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);

        log.info("Token for userId: " + userId + "was revoked");
    }

}