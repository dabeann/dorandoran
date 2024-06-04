package com.backend.dorandoran.assessment.domain.entity;

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
@Table(name = "psychological_assessment_answer")
@Entity
public class PsychologicalAssessmentAnswer extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "psychological_assessment_answer_id")
    private Long id;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psychological_assessment_question_id")
    private PsychologicalAssessmentQuestion question;
}