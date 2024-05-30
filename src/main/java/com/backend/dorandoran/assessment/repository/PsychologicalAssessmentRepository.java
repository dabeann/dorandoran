package com.backend.dorandoran.assessment.repository;

import com.backend.dorandoran.assessment.domain.entity.PsychologicalAssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PsychologicalAssessmentRepository extends JpaRepository<PsychologicalAssessmentQuestion, Long> {
}
