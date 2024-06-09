package com.backend.dorandoran.user.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.user.UserAgency;
import com.backend.dorandoran.common.domain.user.UserRole;
import com.backend.dorandoran.contents.domain.entity.Quotation;
import com.backend.dorandoran.user.domain.request.SmsVerificationRequest;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;

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
    private Disease[] diseases;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation todayQuotation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserMentalState> userMentalStates;

    @Enumerated(EnumType.STRING)
    @Column(name = "agency", nullable = false)
    private UserAgency userAgency;

    public void updateTodayQuotation(Quotation quotation) {
        this.todayQuotation = quotation;
    }

    public void updateDiseasesAndQuotation(Disease[] diseases, Quotation quotation) {
        this.diseases = diseases;
        this.todayQuotation = quotation;
    }

    public static User toUserEntity(SmsVerificationRequest request) {
        return User.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .role(UserRole.ROLE_USER)
                .userAgency(request.userAgency())
                .build();
    }
}
