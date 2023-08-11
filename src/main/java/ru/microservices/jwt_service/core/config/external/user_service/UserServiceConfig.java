package ru.microservices.jwt_service.core.config.external.user_service;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {
    @Value("${grpc.client.user-service.address}")
    private String address;
    @Value("${grpc.client.user-service.port}")
    private Integer port;

    public Channel getChannel() {
        return ManagedChannelBuilder.forAddress(
                this.address,
                this.port
        ).usePlaintext().build();
    }
}
