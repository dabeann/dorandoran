package com.backend.dorandoran.user.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import com.backend.dorandoran.common.domain.UserRole;
import com.backend.dorandoran.contents.domain.entity.Quotation;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import com.backend.dorandoran.user.domain.request.SmsVerificationRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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

    @Type(StringArrayType.class)
    @Column(name = "diseases", columnDefinition = "text[]")
    private String[] diseases = new String[0];

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation todayQuotation;

    public void updateTodayQuotation(Quotation quotation) {
        this.todayQuotation = quotation;
    }

    public static User toUserEntity(SmsVerificationRequest request) {
        return User.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .role(UserRole.ROLE_USER)
                .build();
    }
}
