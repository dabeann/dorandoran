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

    @Column(name = "depression_percent", nullable = false)
    private Integer depressionPercent;

    @Column(name = "stress_percent", nullable = false)
    private Integer stressPercent;

    @Column(name = "anxiety_percent", nullable = false)
    private Integer anxietyPercent;

    public static UserMentalState toUserMentalStateEntity(User user, List<PsychologicalAssessmentResponse.PsychologicalAssessmentResult> results) {
        return UserMentalState.builder()
                .user(user)
                .depression(results.get(0).getScore())
                .stress(results.get(1).getScore())
                .anxiety(results.get(2).getScore())
                .depressionPercent(results.get(0).getPercent())
                .stressPercent(results.get(1).getPercent())
                .anxietyPercent(results.get(2).getPercent())
                .build();
    }

    public static UserMentalState toUserMentalStateEntity(User user, UserMentalState previousMentalState, int[] scores) {
        return UserMentalState.builder()
                .user(user)
                .depression(previousMentalState.getDepression() + scores[0])
                .stress(previousMentalState.getStress() + scores[1])
                .anxiety(previousMentalState.getAnxiety() + scores[2])
                .depressionPercent(previousMentalState.getDepressionPercent() - scores[0])
                .stressPercent(previousMentalState.getStressPercent() - scores[1])
                .anxietyPercent(previousMentalState.getAnxietyPercent() - scores[2])
                .build();
    }
}
