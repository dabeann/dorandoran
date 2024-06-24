package com.backend.dorandoran.mypage.domain.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CompletedCounselResponse(
        Long counselId,
        String title,
        String counselDate
) {
    public CompletedCounselResponse(Long id, String title, LocalDateTime counselDate) {
        this(id, title, formatDateTime(counselDate));
    }

    private static String formatDateTime(LocalDateTime counselDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return counselDate.format(formatter);
    }
}
