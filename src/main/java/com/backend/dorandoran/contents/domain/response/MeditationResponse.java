package com.backend.dorandoran.contents.domain.response;

import com.backend.dorandoran.contents.domain.entity.MeditationContents;

public record MeditationResponse(
        String title,
        String link,
        String thumbNail
) {
    public MeditationResponse(MeditationContents meditationContents) {
        this(
                meditationContents.getTitle(),
                meditationContents.getLink(),
                meditationContents.getThumbnailLink()
        );
    }
}
