package com.backend.dorandoran.mypage.domain.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record CompletedCounselResponse(
        Long counselId,
        String title,
        LocalDate date
) {
    public CompletedCounselResponse(Long id, String title, String createdDateStr) {
        this(id, title, LocalDate.parse(createdDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
