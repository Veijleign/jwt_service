package ru.microservices.jwt_service.core.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Slf4j
@Getter
@Configuration
public class JwtConfig {

    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] decode = Decoders.BASE64.decode(key);

        log.info("Jwt decoder initialized");

        return NimbusJwtDecoder.withSecretKey(
                Keys.hmacShaKeyFor(decode)
        ).build();
    }
}
