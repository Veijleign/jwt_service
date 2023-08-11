package ru.microservices.jwt_service.domain.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.microservices.authentication_service.TokenResponse;
import ru.microservices.authentication_service.ValidateTokenResponse;
import ru.microservices.jwt_service.domain.payload.TokenDTO;


@Component
@RequiredArgsConstructor
public class TokenMapper {

    public TokenResponse toTokenResponse(TokenDTO dto) {
        return TokenResponse.newBuilder()
                .setAccessToken(dto.getAccessToken())
                .setRefreshToken(dto.getRefreshToken())
                .build();
    }

    public ValidateTokenResponse toValidTokenResponse(Boolean value) {
        return ValidateTokenResponse.newBuilder()
                .setValue(value)
                .build();
    }

}
