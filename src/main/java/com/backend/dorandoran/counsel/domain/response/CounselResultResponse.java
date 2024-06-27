package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CounselResultResponse {
    private String result;
    private String summary;
    private List<DialogHistory> messages;
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

    public CounselResultResponse(String result, String summary, List<Dialog> dialogs, List<PsychotherapyContents> psychotherapyContentsList) {
        this.result = result;
        this.summary = summary;
        this.messages = dialogs.stream().map(DialogHistory::new).toList();
        this.contents = psychotherapyContentsList.stream()
                .map(CounselResultPsychotherapyContents::new)
                .toList();
    }
}
