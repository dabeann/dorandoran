package com.backend.dorandoran.mypage.domain.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CompletedCounselResponse(
        Long counselId,
        String title,
        String createdDate
) {
    public CompletedCounselResponse(Long id, String title, LocalDateTime createdDateTime) {
        this(id, title, formatDateTime(createdDateTime));
    }

    private static String formatDateTime(LocalDateTime createdDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return createdDateTime.format(formatter);
    }
}
