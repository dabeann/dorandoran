package com.backend.dorandoran.mypage.controller;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.response.CommonResponse;
import com.backend.dorandoran.mypage.domain.request.PsychologicalChangeTrendRequest;
import com.backend.dorandoran.mypage.domain.response.CompletedCounselResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.domain.response.PsychologicalChangeTrendResponse;
import com.backend.dorandoran.mypage.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "마이페이지", description = "마이페이지 관련 API입니다.")
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@RestController
class MypageController {

    private final MypageService mypageService;

    @Operation(summary = "summary : 마이페이지 메인",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - String name 사용자 이름,
                    - Boolean hasPsychologicalAssessment 심리검사 여부
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/main")
    ResponseEntity<CommonResponse<MypageMainResponse>> getUserInfoForMypageMain() {
        return new ResponseEntity<>(new CommonResponse<>("마이페이지 메인", mypageService.getUserInfoForMypageMain()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 나의 첫 심리검사 결과 조회",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - String name 검사자 이름
                    - String testDate 검사일
                    - List<Object> result 결과 목록
                        - String category 카테고리
                        - Integer score 점수
                        - Integer percent 퍼센트(%)
                        - String standard 기준
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/first-assessment-result")
    ResponseEntity<CommonResponse<PsychologicalAssessmentResponse>> getUserFirstAssessmentResult() {
        return new ResponseEntity<>(new CommonResponse<>("첫 심리검사 결과 조회",
                mypageService.getUserFirstAssessmentResult()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 심리변화 추이 조회",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    - String category (ANXIETY/DEPRESSION/STRESS) 심리 상태 카테고리
                    - Integer month 요청 월
                    ## 응답 :
                    - List<Object> 심리상태 생성일 및 점수 목록
                        - Integer dayOfMonth 상담일
                        - Integer score 점수
                        - Long counselId 상담번호
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/psychological-change-trend")
    ResponseEntity<CommonResponse<List<PsychologicalChangeTrendResponse>>> getUserPsychologicalChangeTrend(
            @Valid @RequestBody PsychologicalChangeTrendRequest request) {
        return new ResponseEntity<>(new CommonResponse<>("심리변화 추이 조회",
                mypageService.getUserPsychologicalChangeTrend(request)), HttpStatus.OK);
    }

    @Operation(summary = "summary : 완료한 상담 조회",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    - Long counselId 상담번호
                    ## 응답 :
                    - Long counselId 상담번호
                    - String title 상담 제목
                    - String counselDate 상담일
                    """)
    @GetMapping("/counsel/{counselId}")
    ResponseEntity<CommonResponse<CompletedCounselResponse>> getCompletedCounsel(
            @PathVariable("counselId") @NotNull Long counselId) {
        return new ResponseEntity<>(new CommonResponse<>("완료한 상담 조회",
                mypageService.getCompletedCounsel(counselId)), HttpStatus.OK);
    }

    @Operation(summary = "summary : 로그아웃",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - String data "Success"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/logout")
    ResponseEntity<CommonResponse<String>> logout() {
        mypageService.logout();
        return new ResponseEntity<>(new CommonResponse<>("로그아웃", "Success"), HttpStatus.OK);
    }

    @Operation(summary = "summary : 회원 탈퇴",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - String data "Success"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/sign-out")
    ResponseEntity<CommonResponse<String>> signOut() {
        mypageService.signOut();
        return new ResponseEntity<>(new CommonResponse<>("회원 탈퇴", "Success"), HttpStatus.OK);
    }
}
