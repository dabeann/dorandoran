package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.counsel.domain.entity.Counsel;
import java.time.LocalDate;
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
        public static List<CounselHistory> fromCounselList(List<Counsel> counselList) {
            return counselList.stream()
                    .map(counsel -> new CounselHistory(
                            counsel.getId(),
                            counsel.getTitle(),
                            counsel.getUpdatedDateTime().toLocalDate()
                    ))
                    .toList();
        }
    }
}
