package ru.microservices.jwt_service.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.lognet.springboot.grpc.security.jwt.JwtAuthProviderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import ru.microservices.authentication_service.AuthenticationServiceGrpc;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends GrpcSecurityConfigurerAdapter {

    private final JwtDecoder jwtDecoder;

    @Override
    public void configure(GrpcSecurity builder) throws Exception {
        builder
                .authorizeRequests()
                .anyMethodExcluding(
                        AuthenticationServiceGrpc.getSignUpMethod(),
                        AuthenticationServiceGrpc.getSignInMethod(),
                        AuthenticationServiceGrpc.getRefreshTokenMethod(),
                        AuthenticationServiceGrpc.getValidateTokenMethod()
                )
                .authenticated()
                .and()
                .authenticationProvider(
                        JwtAuthProviderFactory.forAuthorities(
                                jwtDecoder
                        )
                );
    }
}