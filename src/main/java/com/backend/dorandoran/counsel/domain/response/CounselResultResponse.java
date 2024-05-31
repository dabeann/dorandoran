package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CounselResultResponse {
    private String result;
    private String summary;
    private List<CounselResultPsychotherapyContents> contents;

    @AllArgsConstructor
    @Getter
    public static class CounselResultPsychotherapyContents {
        private String title;
        private String link;
        private String thumbnailLink;

        public CounselResultPsychotherapyContents(PsychotherapyContents psychotherapyContents) {
            this(
                    psychotherapyContents.getTitle(),
                    psychotherapyContents.getLink(),
                    psychotherapyContents.getThumbnailLink()
            );
        }
    }

    public CounselResultResponse(String result, String summary, List<PsychotherapyContents> psychotherapyContentsList) {
        this.result = result;
        this.summary = summary;
        this.contents = psychotherapyContentsList.stream()
                .map(CounselResultPsychotherapyContents::new)
                .toList();
    }
}
