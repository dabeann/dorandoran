package com.backend.dorandoran.assessment.domain.response;

import com.backend.dorandoran.common.domain.PsychologicalAssessmentCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PsychologicalAssessmentResponse {
    private String name;
    private String testDate;
    private List<PsychologicalAssessmentResult> result;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PsychologicalAssessmentResult {
        PsychologicalAssessmentCategory category;
        Integer score;
        Integer percent;
    }
}