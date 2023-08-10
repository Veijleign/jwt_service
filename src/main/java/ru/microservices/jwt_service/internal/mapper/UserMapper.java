package ru.microservices.jwt_service.internal.mapper;

import org.springframework.stereotype.Component;
import ru.microservices.jwt_service.internal.payload.UserDTO;
import ru.microservices.proto.UsersResponse;
import ru.microservices.proto.UserModel;
import ru.microservices.proto.UserResponse;

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
            UsersResponse userListResponse
    ) {
        return userListResponse
                .getUsersList()
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
