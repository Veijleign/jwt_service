package ru.microservices.jwt_service.internal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ETokenType tokenType;

    @Column(nullable = false)
    @Builder.Default
    public Boolean isRevoked = false;

    @Column(nullable = false)
    @Builder.Default
    public Boolean isExpired = false;

    @Column(nullable = false)
    public Long userId;
}
