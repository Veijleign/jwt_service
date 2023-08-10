package ru.microservices.jwt_service.internal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.microservices.jwt_service.internal.entity.Token;
import ru.microservices.jwt_service.internal.payload.TokenDTO;
import ru.microservices.proto.TokenResponse;
import ru.microservices.proto.ValidateTokenResponse;

@Component
@RequiredArgsConstructor
public class TokenMapper {

    public TokenResponse toTokenResponse(TokenDTO dto) {
        return TokenResponse.newBuilder()
                .setAccessToken(dto.getAccessToken())
                .setRefreshToken(dto.getRefreshToken())
                .build();
    }

    public ValidateTokenResponse toValidTokenResponse(Boolean hasAccess) {
        return ValidateTokenResponse.newBuilder()
                .setHasAccess(hasAccess)
                .build();
    }

}
