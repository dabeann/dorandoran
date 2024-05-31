package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.counsel.domain.entity.Counsel;
import java.time.LocalDate;
import java.util.List;

public record CounselHistoryResponse (
        Long counselId,
        String title,
        LocalDate date
){
    public static List<CounselHistoryResponse> fromCounselList(List<Counsel> counselList) {
        return counselList.stream()
                .map(counsel -> new CounselHistoryResponse(
                        counsel.getId(),
                        counsel.getTitle(),
                        counsel.getCreatedDateTime().toLocalDate()
                ))
                .toList();
    }
}
