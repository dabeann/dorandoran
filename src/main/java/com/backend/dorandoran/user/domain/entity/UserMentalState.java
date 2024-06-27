package com.backend.dorandoran.user.domain.entity;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_mental_state")
@Entity
public class UserMentalState extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_mental_state_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "depression", nullable = false)
    private Integer depression;

    @Column(name = "stress", nullable = false)
    private Integer stress;

    @Column(name = "anxiety", nullable = false)
    private Integer anxiety;

    public static UserMentalState toUserMentalStateEntity(User user, List<PsychologicalAssessmentResponse.PsychologicalAssessmentResult> results) {
        return UserMentalState.builder()
                .user(user)
                .depression(results.get(0).getScore())
                .stress(results.get(1).getScore())
                .anxiety(results.get(2).getScore())
                .build();
    }

    public static UserMentalState toUserMentalStateEntity(User user, UserMentalState previousMentalState, int[] scores) {
        int depressionScore = previousMentalState.getDepression() + scores[0];
        int stressScore = previousMentalState.getStress() + scores[1];
        int anxietyScore = previousMentalState.getAnxiety() + scores[2];

        depressionScore = Math.max(0, Math.min(100, depressionScore));
        stressScore = Math.max(0, Math.min(100, stressScore));
        anxietyScore = Math.max(0, Math.min(100, anxietyScore));

        return UserMentalState.builder()
                .user(user)
                .depression(depressionScore)
                .stress(stressScore)
                .anxiety(anxietyScore)
                .build();
    }
}
