package ru.microservices.jwt_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.microservices.jwt_service.internal.entity.ETokenType;
import ru.microservices.jwt_service.internal.payload.UserDTO;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class JwtTools {

    private static final String secretKey = "702ECF979164043FF68BD276F95409E6DC10167123D967F0AB78FA8DBCA02062";
    private static final long jwtExpiration = 86400000;
    private static final long refreshExpiration = 604800000;

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim( ///
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
                .setSubject(dto.getEmail())
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
        return buildToken(Collections.singletonMap("userId", dto.getId()), dto, jwtExpiration);
    }

    // TODO Выпилить полиморфизм из generateToken

    public String generateRefreshToken(
            UserDTO dto
    ) {
        return buildToken(new HashMap<>(), dto, refreshExpiration);
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
        final String username = extractUsername(token);
        return (username.equals(dto.getEmail()) && !isTokenExpired(token));
    }
}

