package com.backend.dorandoran.counsel.domain.request;

import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotNull
        Long counselId,
        String message
) {
}
