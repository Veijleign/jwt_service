package ru.microservices.jwt_service.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.microservices.jwt_service.core.config.CommonConfig;
import ru.microservices.jwt_service.core.config.JwtConfig;
import ru.microservices.jwt_service.domain.payload.UserDTO;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private JwtConfig jwtConfig;
    private CommonConfig commonConfig;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(
                Decoders
                        .BASE64
                        .decode(
                                commonConfig.getKey()
                        )
        );
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimResolver
    ) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDTO dto,
            long expiration
    ) {

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(dto.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDTO dto) {
        return generateToken(new HashMap<>(), dto);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDTO dto
    ) {
        // TODO userId -> Enum ETokenClaimKey
        return buildToken(
                Collections.singletonMap(
                        "userId",
                        dto.getId()
                ),
                dto,
                jwtConfig.getAccessExpiration()
        );
    }

    // TODO Выпилить полиморфизм из generateToken

    public String generateRefreshToken(
            UserDTO dto
    ) {
        return buildToken(
                new HashMap<>(),
                dto,
                jwtConfig.getRefreshExpiration()
        );
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(
            String token,
            UserDTO dto
    ) {
        String username = extractUsername(token);
        return (username.equals(dto.getUsername()) && !isTokenExpired(token));
    }
}
