package com.backend.dorandoran.user.domain.request;

public record LoginRequest(
        String name,
        String phoneNumber
) {
}
