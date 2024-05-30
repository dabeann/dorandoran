package com.backend.dorandoran.assessment.controller;

import com.backend.dorandoran.assessment.service.PsychologicalAssessmentService;
import com.backend.dorandoran.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/assessment")
@RequiredArgsConstructor
@RestController
class PsychologicalAssessmentController {

    private final PsychologicalAssessmentService psychologicalAssessmentService;

    // TODO 심리검사 여부 조회
    ResponseEntity<CommonResponse<Boolean>> hasPsychologicalAssessments() {
        return new ResponseEntity<>(new CommonResponse<>("심리검사 여부 조회",
                psychologicalAssessmentService.hasPsychologicalAssessmentResult()), HttpStatus.OK);
    }

    // TODO 심리검사 문항 및 답변 조회


    // TODO 심리검사 결과 분석
}
