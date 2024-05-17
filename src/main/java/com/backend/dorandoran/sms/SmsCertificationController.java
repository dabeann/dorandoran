package com.backend.dorandoran.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class SmsCertificationController {

    private final SmsCertificationService smsCertificationService;

    @PostMapping("/sms/send")
    ResponseEntity<?> sendSms(@RequestBody SmsCertificationRequest request) {
        try {
            smsCertificationService.sendSms(request);
            return new ResponseEntity<>(request.getPhone(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sms/confirm")
    ResponseEntity<?> smsVerification(@RequestBody SmsCertificationRequest request) {
        try {
            smsCertificationService.verifySms(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
