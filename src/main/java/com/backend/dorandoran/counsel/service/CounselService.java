package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.*;
import com.backend.dorandoran.common.domain.dialog.DialogRole;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import com.backend.dorandoran.counsel.domain.response.*;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.counsel.repository.DialogRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import com.backend.dorandoran.user.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CounselService {

    private final UserRepository userRepository;
    private final CounselRepository counselRepository;
    private final DialogRepository dialogRepository;
    private final UserMentalStateRepository userMentalStateRepository;

    public SuggestHospitalResponse suggestHospitalVisit() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        UserMentalState userMentalState = userMentalStateRepository
                .findFirstByUserOrderByCreatedDateTimeDesc(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MENTAL_STATE));

        boolean suggestVisit = isSuggestVisit(userMentalState);
        String comment = suggestVisit
                ? user.getName() + SuggestComment.UNSTABLE.getKoreanComment()
                : SuggestComment.STABLE.getKoreanComment();
        List<String> phoneNumbers = getPhoneNumbers(suggestVisit);

        return new SuggestHospitalResponse(suggestVisit, comment, phoneNumbers);
    }

    @NotNull
    private static List<String> getPhoneNumbers(boolean suggestVisit) {
        return suggestVisit
                ? Arrays.stream(SuggestCallCenter.values())
                .map(SuggestCallCenter::getPhoneNumber)
                .toList()
                : Collections.emptyList();
    }

    private static boolean isSuggestVisit(UserMentalState mentalState) {
        return mentalState.getDepression() <= 40 || mentalState.getStress() <= 40 || mentalState.getAnxiety() <= 40;
    }

    public StartCounselResponse startCounsel() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        Counsel counsel = Counsel.toCounselEntity(user);
        Counsel savedCounsel = counselRepository.save(counsel);
        Dialog dialog = Dialog.builder()
                .counsel(counsel)
                .role(DialogRole.FROM_CONSULTANT)
                .contents("안녕하세요 " + user.getName() + "님! 어떤 내용이든 좋으니, 저에게 마음편히 이야기해주세요.").build();
        dialogRepository.save(dialog);

        return new StartCounselResponse(savedCounsel.getId(),
                "안녕하세요 " + user.getName() + "님! 어떤 내용이든 좋으니, 저에게 마음편히 이야기해주세요.");
    }

    @NotNull
    private Counsel getNotClosedCounsel(Long counselId) {
        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));
        if (counsel.getState() == CounselState.FINISH_STATE) {
            throw new CommonException(ErrorCode.ALREADY_CLOSED_COUNSEL);
        }
        return counsel;
    }

    public CounselHistoryResponse getCounselHistory(String state) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();
        boolean isPsychTestDone = user.getDiseases() != null;

        CounselState counselState = CounselState.valueOfLowerState(state);
        CommonValidator.notNullOrThrow(counselState, ErrorCode.NOT_FOUND_COUNSEL_STATE);
        List<Counsel> counselListByState = counselRepository.findAllByStateAndUserOrderByCreatedDateTimeDesc(
                counselState, user);

        boolean hasCounselHistory = !counselRepository.findAllByUser(user).isEmpty();
        return new CounselHistoryResponse(isPsychTestDone, hasCounselHistory,
                CounselHistoryResponse.CounselHistory.fromCounselList(counselListByState));
    }

    public ProceedCounselResponse getProceedCounsel(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();
        Counsel counsel = getNotClosedCounsel(counselId);
        return new ProceedCounselResponse(counselId, dialogRepository.findAllByCounselOrderByCreatedDateTimeAsc(counsel));
    }
}
