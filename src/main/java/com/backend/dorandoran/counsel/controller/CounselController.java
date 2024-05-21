package com.backend.dorandoran.counsel.controller;

import com.backend.dorandoran.common.domain.response.CommonResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/counsel")
@RequiredArgsConstructor
@RestController
class CounselController {

    @PostMapping("/langChain")
    ResponseEntity<CommonResponse<String>> getLangChainResult(@RequestBody Map<String, String> payload) {
        String consultationId = payload.get("consultation_id");
        String userMessage = payload.get("message");

        log.info("Received consultation_id: {}", consultationId);
        log.info("Received message: {}", userMessage);

        try {
            log.info("Python 코드 실행 중...");

            ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/java/com/backend/dorandoran/counsel/service/langchain/LangChain.py", consultationId, userMessage);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python 스크립트 실행 중 오류 발생. 종료 코드: " + exitCode);
                log.error("Python 오류 메시지: " + output.toString().trim());
                return new ResponseEntity<>(
                        new CommonResponse<>("Error: Python script execution failed with exit code ",
                                exitCode + "\n" + output.toString().trim()), HttpStatus.BAD_REQUEST);
            }

            log.info("Python 결과: " + output.toString().trim());
            return new ResponseEntity<>(new CommonResponse<>("채팅", output.toString().trim()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Python 스크립트 호출 중 오류 발생: ", e);
            return new ResponseEntity<>(new CommonResponse<>("Error: ", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
