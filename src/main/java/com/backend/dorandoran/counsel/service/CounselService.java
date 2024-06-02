package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.domain.counsel.CounselorType;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.repository.PsychotherapyContentsRepository;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import com.backend.dorandoran.counsel.domain.response.CounselHistoryResponse;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse;
import com.backend.dorandoran.counsel.domain.response.ProceedCounselResponse;
import com.backend.dorandoran.counsel.domain.response.StartCounselResponse;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.counsel.repository.DialogRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CounselService {

    private final UserRepository userRepository;
    private final CounselRepository counselRepository;
    private final PsychotherapyContentsRepository psychotherapyContentsRepository;
    private final DialogRepository dialogRepository;

    public StartCounselResponse startCounsel() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        Counsel counsel = Counsel.builder()
                .user(user)
                .counselorType(CounselorType.COMMON_TYPE)
                .state(CounselState.PROCEED_STATE)
                .build();
        Counsel savedCounsel = counselRepository.save(counsel);

        // TODO 기본 멘트 바뀌면 바꾸기
        return new StartCounselResponse(savedCounsel.getId(),
                "안녕하세요 " + user.getName() + "님! 어떤 이야기든 저에게 말해주세요.");
    }

    public CounselResultResponse endCounsel(String summary) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        List<Disease> diseasesList = Arrays.stream(user.getDiseases())
                .map(Disease::valueOf)
                .toList();

        List<PsychotherapyContents> contentsByCategories = psychotherapyContentsRepository
                .findAllByCategoryIn(diseasesList);
        Collections.shuffle(contentsByCategories);
        List<PsychotherapyContents> limitThreeContents = contentsByCategories.stream().limit(3).toList();

        // TODO 심리 결과
        return new CounselResultResponse("result", summary, limitThreeContents);
    }

    @Transactional
    public void validateBeforeEndCounsel(Long counselId) {
        Counsel counsel = validateBeforeChat(counselId);
        counsel.updateState(CounselState.FINISH_STATE);
    }

    public Counsel validateBeforeChat(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();
        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));
        if (counsel.getState() == CounselState.FINISH_STATE) {
            throw new CommonException(ErrorCode.ALREADY_CLOSED_COUNSEL);
        }
        return counsel;
    }

    public List<CounselHistoryResponse> getCounselHistory(String state) {
        UserInfoUtil.getUserIdOrThrow();
        CounselState counselState = CounselState.valueOfKoreanState(state);
        CommonValidator.notNullOrThrow(counselState, ErrorCode.NOT_FOUND_COUNSEL_STATE);
        List<Counsel> counselListByState = counselRepository.findAllByStateOrderByCreatedDateTimeDesc(counselState);
        return CounselHistoryResponse.fromCounselList(counselListByState);
    }

    public ProceedCounselResponse getProceedCounsel(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();
        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));
        if (counsel.getState() == CounselState.FINISH_STATE) {
            throw new CommonException(ErrorCode.ALREADY_CLOSED_COUNSEL);
        }
        return new ProceedCounselResponse(counselId, dialogRepository.findAllByCounselOrderByCreatedDateTimeAsc(counsel));
    }
}
