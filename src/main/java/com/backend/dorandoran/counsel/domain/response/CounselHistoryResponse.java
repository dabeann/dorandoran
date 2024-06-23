package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.counsel.domain.entity.Counsel;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

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
        public static List<CounselHistory> fromCounselList(List<Counsel> counselList, List<LocalDate> localDateList) {
            return IntStream.range(0, counselList.size())
                    .mapToObj(i -> new CounselHistory(
                            counselList.get(i).getId(),
                            counselList.get(i).getTitle(),
                            localDateList.get(i)
                    ))
                    .toList();
        }
    }
}
