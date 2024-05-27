package com.backend.dorandoran.counsel.domain.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounselResultResponse {

    private String result;
    private String summary;
    private List<CounselResultPsychotherapyContents> contents;

    @Getter
    @AllArgsConstructor
    public static class CounselResultPsychotherapyContents {

        private String title;
        private String link;
        private String thumbnailLink;
    }
}
