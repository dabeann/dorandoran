package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.common.domain.DialogRole;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.counsel.repository.DialogRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CounselService {

    private final DialogRepository dialogRepository;
    private final CounselRepository counselRepository;

    public String getChatResult(String counselId) {
        Optional<Counsel> counsel = counselRepository.findById(Long.parseLong(counselId));
        Optional<Dialog> dialog = dialogRepository.findFirstByCounselAndRoleOrderByCreatedDateTimeDesc(
                counsel.get(),
                DialogRole.FROM_CONSULTANT);
        // TODO 유효성 검사
        return dialog.get().getContents();
    }
}
