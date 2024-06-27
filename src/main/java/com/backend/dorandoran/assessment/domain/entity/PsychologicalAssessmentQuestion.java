package com.backend.dorandoran.assessment.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentCategory;
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
@Table(name = "psychological_assessment_question")
@Entity
public class PsychologicalAssessmentQuestion extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "psychological_assessment_question_id")
    private Long id;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PsychologicalAssessmentCategory category;

    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<PsychologicalAssessmentAnswer> answers;
}