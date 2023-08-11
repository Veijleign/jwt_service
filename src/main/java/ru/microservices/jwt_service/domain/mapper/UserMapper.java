package ru.microservices.jwt_service.domain.mapper;

import org.springframework.stereotype.Component;
import ru.microservices.jwt_service.domain.payload.UserDTO;
import ru.microservices.user_service.*;

import java.util.List;

@Component
public class UserMapper {

    private UserDTO toDTO(UserModel userModel) {
        return new UserDTO(
                userModel.getId(),
                userModel.getUsername()
        );
    }

    public UserDTO toDTO(UserResponse userResponse) {
        return toDTO(
                userResponse.getUser()
        );
    }

    public List<UserDTO> toDTOs(
            UserMultipleResponse userMultipleResponse
    ) {
        return userMultipleResponse
                .getUsersList()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CreateUserRequest toCreateUserRequest(String username, String password) {
        return CreateUserRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
    }

    public ValidateUserRequest toValidateUserRequest(String username, String password) {
        return ValidateUserRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
    }

    public Boolean fromValidateUserResponse(ValidateUserResponse response) {
        return response.getValue();
    }

    public UserUsernameRequest toUserUsernameRequest(String username) {
        return UserUsernameRequest.newBuilder()
                .setUsername(username)
                .build();
    }


}
