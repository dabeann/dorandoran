package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.counsel.domain.entity.Dialog;
import java.util.List;
import lombok.Getter;

@Getter
public class ProceedCounselResponse {
    private Long counselId;
    private List<DialogHistory> messages;

    public ProceedCounselResponse(Long counselId, List<Dialog> dialogs) {
        this.counselId = counselId;
        this.messages = dialogs.stream().map(DialogHistory::new).toList();
    }
}
