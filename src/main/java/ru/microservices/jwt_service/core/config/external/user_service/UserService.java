package ru.microservices.jwt_service.core.config.external.user_service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.microservices.user_service.UserServiceGrpc;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserServiceConfig config;

    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub() {
        return UserServiceGrpc.newBlockingStub(
                config.getChannel()
        );
    }
}
