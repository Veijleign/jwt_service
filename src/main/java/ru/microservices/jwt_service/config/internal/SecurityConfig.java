package ru.microservices.jwt_service.config.internal;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.lognet.springboot.grpc.security.jwt.JwtAuthProviderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import ru.microservices.proto.AuthServiceGrpc;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends GrpcSecurityConfigurerAdapter {

    private static final String jwtSecret = "702ECF979164043FF68BD276F95409E6DC10167123D967F0AB78FA8DBCA02062";

    @Bean
    public JwtDecoder jwtDecoder() {
        var key = Decoders.BASE64.decode(jwtSecret);
        return NimbusJwtDecoder.withSecretKey(Keys.hmacShaKeyFor(key)).build();
    }

    @Override
    public void configure(GrpcSecurity builder) throws Exception {
        builder
                .authorizeRequests()
                .anyMethodExcluding(
                        AuthServiceGrpc.getSignUpMethod(),
                        AuthServiceGrpc.getSignInMethod(),
                        AuthServiceGrpc.getRefreshTokenMethod(),
                        AuthServiceGrpc.getValidateTokenMethod()
                )
                .authenticated()
                .and()
                .authenticationProvider(JwtAuthProviderFactory.forAuthorities(jwtDecoder()));
    }
}