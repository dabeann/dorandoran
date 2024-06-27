package com.backend.dorandoran.counsel.domain.response;

import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import java.util.List;
import lombok.Getter;

@Getter
public class FinishCounselResponse {
    private Long counselId;
    private String result;
    private String summary;
    private List<DialogHistory> messages;

    public FinishCounselResponse(Counsel counsel, List<Dialog> dialogs) {
        this.counselId = counsel.getId();
        this.result = counsel.getResult();
        this.summary = counsel.getSummary();
        this.messages = dialogs.stream().map(DialogHistory::new).toList();
    }
}
