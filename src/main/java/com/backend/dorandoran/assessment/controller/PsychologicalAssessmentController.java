package com.backend.dorandoran.assessment.controller;

import com.backend.dorandoran.assessment.domain.request.PsychologicalAssessmentRequest;
import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.assessment.service.PsychologicalAssessmentService;
import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "심리검사", description = "심리검사 관련 API입니다.")
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
@RestController
class PsychologicalAssessmentController {

    private final PsychologicalAssessmentService psychologicalAssessmentService;

    @Operation(summary = "summary : 심리검사 여부 조회",
            description = """
                    ## 요청 :
                    - Header (Authorization Bearer 토큰) (필수)
                    ## 응답 :
                    - String message
                    - Boolean data 심리검사 여부
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/has-result")
    ResponseEntity<CommonResponse<Boolean>> hasPsychologicalAssessmentResult() {
        return new ResponseEntity<>(new CommonResponse<>("심리검사 여부 조회",
                psychologicalAssessmentService.hasPsychologicalAssessmentResult()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 심리검사 결과 분석",
            description = """
                    ## 요청 :
                    - Header (Authorization Bearer 토큰) (필수)
                    - Object category (BASIC, STRESS, DEPRESSION, ANXIETY) 문항 카테고리
                    - List<Object> questionAnswers 문항 답변 리스트
                        - Integer questionId 문항 번호
                        - Integer answerId 답변 번호
                    - 예시
                        {
                            "category": "BASIC",
                            "questionAnswers": [
                                    {
                                        "questionId": 1,
                                        "answerId": 4
                                    },
                                    {
                                        "questionId": 2,
                                        "answerId": 3
                                    },
                                    {
                                        "questionId": 3,
                                        "answerId": 45
                                    }
                            ]
                        }
                    ## 응답 :
                    - String name 검사자 이름
                    - String testDate 검사일
                    - List<Object> result 결과 목록
                        - String category 카테고리
                        - Integer score 점수
                        - Integer percent 퍼센트(%)
                    - 예시
                        {
                            "message": "심리검사 결과 분석",
                            "data": {
                                "name": "박다정",
                                "testDate": "2024년 06월 03일",
                                "result": [
                                    {
                                        "category": "DEPRESSION",
                                        "score": 67,
                                        "percent": 33
                                    },
                                    {
                                        "category": "STRESS",
                                        "score": 60,
                                        "percent": 40
                                    },
                                    {
                                        "category": "ANXIETY",
                                        "score": 73,
                                        "percent": 27
                                    }
                                ]
                            }
                        }
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/analysis")
    ResponseEntity<CommonResponse<PsychologicalAssessmentResponse>> analysisPsychologicalAssessment(
            @RequestBody List<PsychologicalAssessmentRequest> requests) {
        return new ResponseEntity<>(new CommonResponse<>("심리검사 결과 분석",
                psychologicalAssessmentService.analysisPsychologicalAssessment(requests)), HttpStatus.OK);
    }
}
