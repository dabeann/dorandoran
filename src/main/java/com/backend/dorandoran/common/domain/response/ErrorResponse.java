package com.backend.dorandoran.common.domain.response;

import com.backend.dorandoran.common.domain.ErrorCode;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        ErrorCode errorCode,
        String message
) {
    public static ResponseEntity<ErrorResponse> ErrorResponse(ErrorCode errorCode, String errorMessage) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorResponse(errorCode, errorMessage));
    }
}
