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
        this.result = processAssessmentResult(anxiety, depression, stress);;
    }

    private static List<PsychologicalAssessmentResult> processAssessmentResult(Integer anxiety, Integer depression, Integer stress) {
        List<PsychologicalAssessmentResult> results = new ArrayList<>();
        results.add(PsychologicalAssessmentResult.builder()
                .category(PsychologicalAssessmentCategory.DEPRESSION)
                .score(depression)
                .percent(100 - depression)
                .standard(calculateStandard(100 - depression, PsychologicalAssessmentCategory.DEPRESSION))
                .build());
        results.add(PsychologicalAssessmentResult.builder()
                .category(PsychologicalAssessmentCategory.STRESS)
                .score(stress)
                .percent(100 - stress)
                .standard(calculateStandard(100 - stress, PsychologicalAssessmentCategory.STRESS))
                .build());
        results.add(PsychologicalAssessmentResult.builder()
                .category(PsychologicalAssessmentCategory.ANXIETY)
                .score(anxiety)
                .percent(100 - anxiety)
                .standard(calculateStandard(100 - anxiety, PsychologicalAssessmentCategory.ANXIETY))
                .build());
        return results;
    }

    private static PsychologicalAssessmentStandard calculateStandard(Integer score, PsychologicalAssessmentCategory category) {
        int mediumMinScore = (category.equals(PsychologicalAssessmentCategory.STRESS)) ? 32 : 36;
        int mediumMaxScore = (category.equals(PsychologicalAssessmentCategory.STRESS)) ? 80 : 75;

        PsychologicalAssessmentStandard standard = PsychologicalAssessmentStandard.적음;
        if (mediumMinScore <= score && mediumMaxScore >= score) {
            standard = PsychologicalAssessmentStandard.중간;
        } else if (mediumMaxScore < score) {
            standard = PsychologicalAssessmentStandard.심각;
        }
        return standard;
    }
}