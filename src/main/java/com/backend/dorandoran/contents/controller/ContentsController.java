package com.backend.dorandoran.contents.controller;

import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.contents.domain.response.ContentsResponse;
import com.backend.dorandoran.contents.service.ContentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contents", description = "콘텐츠 관련 API입니다.")
@RequestMapping("/api/contents")
@RequiredArgsConstructor
@RestController
class ContentsController {

    private final ContentsService contentsService;

    @Operation(summary = "summary : 콘텐츠 메인",
            description = """
                    ## 요청 :
                    - header token (필수)
                    - string {category} (필수X)
                        - '당신을 위한 콘텐츠' 일 경우 null(빈 값)
                        - 그 외 경우 병명 넘기기 (우울증, 스트레스, 불안증, 알코올_중독, 흡연_중독)
                    ## 응답 :
                    - string 명언
                    - 심리 치료 리스트
                        - string 제목
                        - string 링크
                        - string 썸네일 링크
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/main/{category}")
    ResponseEntity<CommonResponse<ContentsResponse>> getMainContents(@PathVariable(name = "category", required = false) String category) {
        return new ResponseEntity<>(new CommonResponse<>("콘텐츠 메인", contentsService.getMainContents(category)), HttpStatus.OK);
    }
}