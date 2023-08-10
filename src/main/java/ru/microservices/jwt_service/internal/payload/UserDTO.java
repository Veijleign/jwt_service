package ru.microservices.jwt_service.internal.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;

    public UserDTO(long id,
                   String email
    ) {
        this.id = id;
        this.email = email;
    }

}
