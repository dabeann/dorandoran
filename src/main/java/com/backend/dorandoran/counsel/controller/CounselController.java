package com.backend.dorandoran.counsel.controller;

import com.backend.dorandoran.common.domain.response.BasicApiSwaggerResponse;
import com.backend.dorandoran.common.domain.response.CommonResponse;
import com.backend.dorandoran.counsel.domain.request.ChatRequest;
import com.backend.dorandoran.counsel.domain.response.*;
import com.backend.dorandoran.counsel.service.CounselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Tag(name = "상담", description = "상담 관련 API입니다.")
@RequestMapping("/api/counsel")
@RequiredArgsConstructor
@RestController
class CounselController {

    private final CounselService counselService;

    @Operation(summary = "summary : 전문 상담 제안",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    ## 응답 :
                    - Boolean suggestVisit (true: 심리 상태 불안정, false: 심리 상태 안정)
                    - String comment (suggestVisit이 false인 경우 "" 리턴)
                    - List<String> phoneNumbers (suggestVisit이 false인 경우 빈 리스트 리턴)
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/suggest")
    ResponseEntity<CommonResponse<SuggestHospitalResponse>> suggestHospitalVisit() {
        return new ResponseEntity<>(new CommonResponse<>("전문 상담 제안",
                counselService.suggestHospitalVisit()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 상담 채팅",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - Long counselId (필수)
                    - String message (필수)
                    ## 응답 :
                    - String "gpt 상담자의 답변"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @PostMapping("/chat")
    ResponseEntity<CommonResponse<String>> getChatResult(@Valid @RequestBody ChatRequest request) {
        counselService.validateBeforeChat(request.counselId());

        String counselId = String.valueOf(request.counselId());
        String userMessage = request.message();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/python3",
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
            String resultMessage = counselService.sendEmergencySms(result, request.counselId());

            return new ResponseEntity<>(new CommonResponse<>("상담 채팅", resultMessage), HttpStatus.OK);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            return new ResponseEntity<>(new CommonResponse<>("Error: ", stackTrace), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "summary : 상담 시작",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    ## 응답 :
                    - Long counselId
                    - String message
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/start")
    ResponseEntity<CommonResponse<StartCounselResponse>> startCounsel() {
        return new ResponseEntity<>(new CommonResponse<>("상담 시작",
                counselService.startCounsel()), HttpStatus.OK);
    }

    @Operation(summary = "summary : 상담 종료 & 종료된 상담 클릭",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - {counselId} (필수)
                    ## 응답 :
                    - String result
                    - String summary
                    - messages : [{String role, String message, LocalDate date}, ...]
                    - contents : [{String title, String link, String thumbnailLink}, ...]
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/end/{counselId}")
    ResponseEntity<CommonResponse<CounselResultResponse>> endCounsel(@PathVariable("counselId") @NotNull Long counselId) {
        if (counselService.isFinishedCounsel(counselId)) {
            return new ResponseEntity<>(new CommonResponse<>("종료된 상담 클릭",
                    counselService.getFinishCounselWithDialog(counselId)), HttpStatus.OK);
        } else {
            counselService.validateBeforeEndCounsel(counselId);
            try {

                ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/python3",
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

                String resultWithSummary = output.toString().trim();

                return new ResponseEntity<>(new CommonResponse<>("상담 결과",
                        counselService.endCounsel(counselId, resultWithSummary)), HttpStatus.OK);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTrace = sw.toString();
                return new ResponseEntity<>(new CommonResponse<>("Error: " + stackTrace, null),
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Operation(summary = "summary : 상담 내역",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - String {state} (필수)
                        - "counsel" (진행중)
                        - "complete" (종료)
                    ## 응답 :
                    - Boolean isPsychTestDone 심리 검사 여부
                    - Boolean hasCounselHistory 상담(종료,진행중 모두) 있는지 여부
                    - List counselHistories [{Long counselId,
                               String title,
                               LocalDate date}, ...]
                    - 해당 상태의 상담이 하나도 없다면 empty list 반환
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/history/{state}")
    ResponseEntity<CommonResponse<CounselHistoryResponse>> getCounselHistory(
            @PathVariable("state") @NotBlank String state) {
        return new ResponseEntity<>(new CommonResponse<>("상담 내역",
                counselService.getCounselHistory(state)), HttpStatus.OK);
    }

    @Operation(summary = "summary : 진행중 상담 클릭",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - Long {counselId} (필수)
                    ## 응답 :
                    - Long counselId
                    - messages [{String role, String message}, ...]
                        - role: "상담원" or "내담자"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/proceed/{counselId}")
    ResponseEntity<CommonResponse<ProceedCounselResponse>> getProceedCounsel(
            @PathVariable("counselId") @NotNull Long counselId) {
        return new ResponseEntity<>(new CommonResponse<>("진행중 상담 클릭",
                counselService.getProceedCounsel(counselId)), HttpStatus.OK);
    }

    /*@Operation(summary = "summary : 종료된 상담 클릭",
            description = """
                    ## 요청 :
                    - Header token (필수)
                    - Long {counselId} (필수)
                    ## 응답 :
                    - Long counselId
                    - String result
                    - String summary
                    - messages [{String role, String message}, ...]
                        - role: "상담원" or "내담자"
                    """)
    @BasicApiSwaggerResponse
    @ApiResponse(responseCode = "200")
    @GetMapping("/finish/{counselId}")
    ResponseEntity<CommonResponse<FinishCounselResponse>> getFinishCounsel(
            @PathVariable("counselId") @NotNull Long counselId) {
        return new ResponseEntity<>(new CommonResponse<>("종료된 상담 클릭",
                counselService.getFinishCounsel(counselId)), HttpStatus.OK);
    }*/
}
