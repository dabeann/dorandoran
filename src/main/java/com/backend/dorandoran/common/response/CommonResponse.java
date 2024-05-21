package com.backend.dorandoran.common.response;

public record CommonResponse<T> (
        String message,
        T data
) {
}
