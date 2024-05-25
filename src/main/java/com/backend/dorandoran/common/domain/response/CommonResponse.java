package com.backend.dorandoran.common.domain.response;

public record CommonResponse<T>(
        String message,
        T data
) {
}
