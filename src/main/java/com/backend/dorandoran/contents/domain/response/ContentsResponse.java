package com.backend.dorandoran.contents.domain.response;

import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ContentsResponse {
    private String quotation;
    private List<PsychotherapyContentsResponse> psychotherapyContents;

    @AllArgsConstructor
    @Getter
    public static class PsychotherapyContentsResponse{
        private String title;
        private String link;
        private String thumbnailLink;

        public PsychotherapyContentsResponse(PsychotherapyContents psychotherapyContents) {
            this(
                    psychotherapyContents.getTitle(),
                    psychotherapyContents.getLink(),
                    psychotherapyContents.getThumbnailLink()
            );
        }
    }

    public ContentsResponse(String quotation, List<PsychotherapyContents> psychotherapyContentsList) {
        this.quotation = quotation;
        this.psychotherapyContents = psychotherapyContentsList.stream()
                .map(PsychotherapyContentsResponse::new)
                .toList();
    }
}
