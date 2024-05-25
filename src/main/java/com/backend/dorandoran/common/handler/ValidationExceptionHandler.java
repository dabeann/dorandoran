package com.backend.dorandoran.common.handler;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.common.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<CommonResponse<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        return new ResponseEntity<>(new CommonResponse<>(ErrorCode.INVALID_REQUEST.getErrorMessage(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommonException.class)
    ResponseEntity<CommonResponse<ErrorCode>> handleCommonException(CommonException e) {
        return new ResponseEntity<>(new CommonResponse<>(e.getErrorCode().getErrorMessage(), e.getErrorCode()), HttpStatus.BAD_REQUEST);
    }
}