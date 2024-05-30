package com.backend.dorandoran.user.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
