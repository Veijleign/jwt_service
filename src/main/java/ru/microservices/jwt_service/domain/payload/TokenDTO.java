package ru.microservices.jwt_service.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
}
