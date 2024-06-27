package com.backend.dorandoran.assessment.domain.request;

import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentCategory;

import java.util.List;

public record PsychologicalAssessmentRequest(
   PsychologicalAssessmentCategory category,
   List<PsychologicalAssessmentQuestionAnswer> questionAnswers
) {
    public record PsychologicalAssessmentQuestionAnswer (
            Integer questionId,
            Integer answerId
    ) {}
}
