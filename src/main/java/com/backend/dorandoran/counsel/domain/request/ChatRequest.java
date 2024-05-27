package com.backend.dorandoran.counsel.domain.request;

public record ChatRequest(
        Long counselId,
        String message
) {
}
