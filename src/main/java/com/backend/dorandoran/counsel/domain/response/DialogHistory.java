package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.counsel.domain.entity.Dialog;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DialogHistory {
    private String role;
    private String message;

    public DialogHistory(Dialog dialog) {
        this(
                dialog.getRole().getKoreanName(),
                dialog.getContents()
        );
    }
}
