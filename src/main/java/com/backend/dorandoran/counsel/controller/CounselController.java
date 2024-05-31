package com.backend.dorandoran.counsel.controller;

import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.counsel.domain.request.ChatRequest;
import com.backend.dorandoran.counsel.domain.response.CounselHistoryResponse;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse;
import com.backend.dorandoran.counsel.domain.response.StartCounselResponse;
import com.backend.dorandoran.counsel.service.CounselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Counsel", description = "상담 관련 API입니다.")
@RequestMapping("/api/counsel")
@RequiredArgsConstructor
@RestController
class CounselController {

    private final CounselService counselService;

    @Operation(summary = "summary : 상담 채팅",
            description = """
                    ## 요청 :
                    - header token (필수)
                    - Long counselId (필수)
                    - String message (필수)
                    ## 응답 :
                    - String "gpt 상담자의 답변"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/chat")
    ResponseEntity<CommonResponse<String>> getChatResult(@RequestBody ChatRequest request) {
        counselService.validateBeforeChat(request.counselId());

        String counselId = String.valueOf(request.counselId());
        String userMessage = request.message();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python",
                    "src/main/java/com/backend/dorandoran/counsel/service/python/LangChainEntireDialog.py",
                    counselId, userMessage);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return new ResponseEntity<>(
                        new CommonResponse<>("Error: Python script execution failed with exit code ",
                                exitCode + "\n" + output.toString().trim()), HttpStatus.BAD_REQUEST);
            }

            String result = output.toString().trim();
            return new ResponseEntity<>(new CommonResponse<>("상담 채팅", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CommonResponse<>("Error: ", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "summary : 상담 시작",
            description = """
                    ## 요청 :
                    - header token (필수)
                    ## 응답 :
                    - Long counselId
                    - String message
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/start")
    ResponseEntity<CommonResponse<StartCounselResponse>> startCounsel() {
        return new ResponseEntity<>(new CommonResponse<>("상담 시작", counselService.startCounsel()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 상담 종료",
            description = """
                    ## 요청 :
                    - header token (필수)
                    - {counselId} (필수)
                    ## 응답 :
                    - String result
                    - String summary
                    - contents : [{String title, String link, String thumbnailLink}, ...]
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/end/{counselId}")
    ResponseEntity<CommonResponse<CounselResultResponse>> endCounsel(@PathVariable("counselId") Long counselId) {
        counselService.validateBeforeEndCounsel(counselId);
        try {

            ProcessBuilder processBuilder = new ProcessBuilder("python",
                    "src/main/java/com/backend/dorandoran/counsel/service/python/EndCounsel.py",
                    String.valueOf(counselId));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return new ResponseEntity<>(
                        new CommonResponse<>("Error: Python script execution failed with exit code "
                                + exitCode + "\n" + output.toString().trim(), null), HttpStatus.BAD_REQUEST);
            }

            String summary = output.toString().trim();

            return new ResponseEntity<>(new CommonResponse<>("상담 결과",
                    counselService.endCounsel(summary)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CommonResponse<>("Error: " + e, null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "summary : 상담 내역",
            description = """
                    ## 요청 :
                    - header token (필수)
                    - {state} (필수)
                        - "진행중"
                        - "종료"
                    ## 응답 :
                    - List [{Long counselId,
                               String title,
                               LocalDate date}, ...]
                    - 해당 상담이 하나도 없다면 empty list 반환
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/history/{state}")
    ResponseEntity<CommonResponse<List<CounselHistoryResponse>>> getCounselHistory(
            @PathVariable("state") String state) {
        return new ResponseEntity<>(new CommonResponse<>("상담 내역", counselService.getCounselHistory(state)),
                HttpStatus.OK);
    }
}
