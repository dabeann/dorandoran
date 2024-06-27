package com.backend.dorandoran.counsel.domain.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record CounselHistoryResponse (
        boolean isPsychTestDone,
        boolean hasCounselHistory,
        List<CounselHistory> counselHistories
){
    public record CounselHistory(
            Long counselId,
            String title,
            LocalDate date
    ) {
        public CounselHistory(Long counselId, String title, String createdDateStr) {
            this(counselId, title, LocalDate.parse(createdDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
    }
}
