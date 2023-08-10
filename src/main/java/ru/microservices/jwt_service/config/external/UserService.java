package ru.microservices.jwt_service.config.external;

import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.microservices.proto.UserServiceGrpc;

@Service
@RequiredArgsConstructor
public class UserService {

    public UserServiceGrpc.UserServiceBlockingStub defaultChannelBlockingStub() {

        return UserServiceGrpc
                .newBlockingStub(ManagedChannelBuilder
                        .forAddress("localhost", 9092)
                        .usePlaintext()
                        .build()
        );
    }
}
