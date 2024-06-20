package com.backend.dorandoran.assessment.domain.response;

import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentCategory;
import com.backend.dorandoran.common.domain.assessment.PsychologicalAssessmentStandard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        PsychologicalAssessmentStandard standard;
    }

    public PsychologicalAssessmentResponse(String name, LocalDateTime testDate, Integer anxiety, Integer depression, Integer stress) {
        this.name = name;
        this.testDate = testDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        List<PsychologicalAssessmentResponse.PsychologicalAssessmentResult> results = new ArrayList<>();
        results.add(PsychologicalAssessmentResult.builder()
                .category(PsychologicalAssessmentCategory.ANXIETY)
                .score(anxiety)
                .percent(100 - anxiety)
                .build());
        results.add(PsychologicalAssessmentResult.builder()
                .category(PsychologicalAssessmentCategory.DEPRESSION)
                .score(depression)
                .percent(100 - depression)
                .build());
        results.add(PsychologicalAssessmentResponse.PsychologicalAssessmentResult.builder()
                .category(PsychologicalAssessmentCategory.STRESS)
                .score(stress)
                .percent(100 - stress)
                .build());
        this.result = results;
    }
}