package com.backend.dorandoran.counsel.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.domain.counsel.CounselorType;
import com.backend.dorandoran.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "counsel")
@Entity
public class Counsel extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "counsel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "counselor_type")
    private CounselorType counselorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private CounselState state;

    @Column(name = "result")
    private String result;

    @Column(name = "summary", length = 1024)
    private String summary;

    @Column(name = "title")
    private String title;

    public void updateState(CounselState state) {
        this.state = state;
    }

    public void updateResult(String result) {
        this.result = result;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }

    public static Counsel toCounselEntity(User user) {
        return Counsel.builder()
                .user(user)
                .counselorType(CounselorType.COMMON_TYPE)
                .state(CounselState.PROCEED_STATE)
                .build();
    }
}
