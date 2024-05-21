package com.backend.dorandoran.user.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import com.backend.dorandoran.common.domain.UserRole;
import com.backend.dorandoran.user.domain.request.LoginRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "public")
@Entity
public class User extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public static User toUserEntity(LoginRequest request) {
        return User.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .role(UserRole.ROLE_USER)
                .build();
    }
}
