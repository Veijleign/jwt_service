package ru.microservices.jwt_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id = UUID.randomUUID();

    @Column(unique = true, nullable = false)
    public String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ETokenType tokenType;

    @Column(nullable = false)
    public Long userId;

    @Column(nullable = false)
    @Builder.Default
    public Boolean isRevoked = false;

    @Column(nullable = false)
    @Builder.Default
    public Boolean isExpired = false;

}
