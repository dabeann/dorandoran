package com.backend.dorandoran.assessment.controller;

import com.backend.dorandoran.assessment.service.PsychologicalAssessmentService;
import com.backend.dorandoran.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/assessment")
@RequiredArgsConstructor
@RestController
class PsychologicalAssessmentController {

    private final PsychologicalAssessmentService psychologicalAssessmentService;

    @GetMapping("/has-assessment-result")
    ResponseEntity<CommonResponse<Boolean>> hasPsychologicalAssessmentResult() {
        return new ResponseEntity<>(new CommonResponse<>("심리검사 여부 조회",
                psychologicalAssessmentService.hasPsychologicalAssessmentResult()), HttpStatus.OK);
    }

    // TODO 심리검사 문항 및 답변 조회


    // TODO 심리검사 결과 분석
    // -> 심리상태에 점수, 퍼센트 저장
    // -> user에 병명 및 병명에서 오늘의 명언 랜덤으로 저장
}
