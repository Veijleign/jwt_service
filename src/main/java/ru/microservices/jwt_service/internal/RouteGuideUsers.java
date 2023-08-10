package ru.microservices.jwt_service.internal;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.microservices.jwt_service.internal.mapper.UserMapper;
import ru.microservices.jwt_service.internal.payload.UserDTO;
import ru.microservices.jwt_service.util.JwtTools;
import ru.microservices.jwt_service.config.external.UserService;
import ru.microservices.proto.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RouteGuideUsers {

    private final UserService userService;
    private final UserMapper mapper;

    public UserDTO getUserById(Long id) {
        return mapper.toDTO(
                userService.defaultChannelBlockingStub()
                        .getById(
                                GetUserByIdRequest.newBuilder()
                                        .setId(id)
                                        .build()
                        )
        );
    }

    public UserDTO getUserByUsername(String username) {
        return mapper.toDTO(
                userService.defaultChannelBlockingStub()
                        .getUserByUsername(
                                GetUserByUsernameRequest.newBuilder()
                                        .setUsername(username)
                                        .build()
                        )
        );
    }

    public List<UserDTO> getAllUsers() {
        return mapper.toDTOs(
                userService.defaultChannelBlockingStub()
                        .getAllUser(
                                Empty.newBuilder()
                                        .build()
                        )
        );
    }

    public void addUser(String username, String password) {
        userService.defaultChannelBlockingStub().create(
                CreateUserRequest.newBuilder()
                        .setUsername(username)
                        .setPassword(password)
                        .build()
        );
    }

    public boolean validateUser(String username, String password) {
        ValidateResponse response = userService.defaultChannelBlockingStub()
                .validateUser(
                        ValidateRequest.newBuilder()
                                .setUsername(username)
                                .setPassword(password)
                                .build()
                );
        return response.getValue();
    }
}
