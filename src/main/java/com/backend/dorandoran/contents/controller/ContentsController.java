package com.backend.dorandoran.contents.controller;

import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.contents.domain.response.ContentsResponse;
import com.backend.dorandoran.contents.domain.response.MeditationResponse;
import com.backend.dorandoran.contents.service.ContentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "콘텐츠", description = "콘텐츠 관련 API입니다.")
@RequestMapping("/api/contents")
@RequiredArgsConstructor
@RestController
class ContentsController {

    private final ContentsService contentsService;

    @Operation(summary = "summary : 콘텐츠 메인",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - String {category} (필수)
                        - "depression", "stress", "anxiety", "alcoholism", "smoking", "personal"
                    ## 응답 :
                    - String 명언 (심리검사 안 한 경우 null)
                    - 심리 치료 리스트
                        - String 제목
                        - String 링크
                        - String 썸네일 링크
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/main/{category}")
    ResponseEntity<CommonResponse<ContentsResponse>> getMainContents(
            @PathVariable(name = "category", required = false) String category) {
        return new ResponseEntity<>(new CommonResponse<>("콘텐츠 메인",
                contentsService.getMainContents(category)), HttpStatus.OK);
    }

    @Operation(summary = "summary : 명상 시청",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - String {duration} (필수)
                        - duration : 3, 5, 10, 30, 60
                    ## 응답 :
                    - String 제목
                    - String 링크
                    - String 썸네일
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/meditation/{duration}")
    ResponseEntity<CommonResponse<MeditationResponse>> getMeditationContent(
            @PathVariable(name = "duration")
            @NotNull @Digits(integer = 2, fraction = 0, message = "숫자만 입력 가능합니다") Integer duration) {
        return new ResponseEntity<>(new CommonResponse<>("명상 시청",
                contentsService.getMeditationContent(duration)), HttpStatus.OK);
    }
}