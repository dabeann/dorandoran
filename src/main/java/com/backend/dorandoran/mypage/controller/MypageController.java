package com.backend.dorandoran.mypage.controller;

import com.backend.dorandoran.assessment.domain.response.PsychologicalAssessmentResponse;
import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.response.CommonResponse;
import com.backend.dorandoran.mypage.domain.response.MypageMainResponse;
import com.backend.dorandoran.mypage.service.MypageService;
import com.backend.dorandoran.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이페이지", description = "마이페이지 관련 API입니다.")
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@RestController
class MypageController {

    private final UserService userService;
    private final MypageService mypageService;

    @Operation(summary = "summary : 마이페이지 메인",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - Long userId 사용자 고유번호
                    - String name 사용자 이름
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
    @GetMapping("/first-assessment-result")
    ResponseEntity<CommonResponse<PsychologicalAssessmentResponse>> getUserFirstAssessmentResult() {
        return new ResponseEntity<>(new CommonResponse<>("첫 심리검사 결과 조회",
                mypageService.getUserFirstAssessmentResult()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 로그아웃",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - 200, "Success"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/logout")
    ResponseEntity<CommonResponse<String>> logout() {
        userService.logout();
        return new ResponseEntity<>(new CommonResponse<>("로그아웃", "Success"), HttpStatus.OK);
    }

    @Operation(summary = "summary : 회원 탈퇴",
            description = """
                    ## 요청 :
                    - Header(Authorization Bearer *토큰* (필수))
                    ## 응답 :
                    - 200, "Success"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/sign-out")
    ResponseEntity<CommonResponse<String>> signOut() {
        userService.signOut();
        return new ResponseEntity<>(new CommonResponse<>("회원 탈퇴", "Success"), HttpStatus.OK);
    }
}
