package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.CounselResult;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.domain.counsel.SuggestCallCenter;
import com.backend.dorandoran.common.domain.counsel.SuggestComment;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.repository.querydsl.PsychotherapyContentsQueryRepository;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.response.CounselHistoryResponse;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse;
import com.backend.dorandoran.counsel.domain.response.FinishCounselResponse;
import com.backend.dorandoran.counsel.domain.response.ProceedCounselResponse;
import com.backend.dorandoran.counsel.domain.response.StartCounselResponse;
import com.backend.dorandoran.counsel.domain.response.SuggestHospitalResponse;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.counsel.repository.DialogRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import com.backend.dorandoran.user.repository.UserRepository;
import com.backend.dorandoran.user.service.SmsUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CounselService {

    private final UserRepository userRepository;
    private final CounselRepository counselRepository;
    private final PsychotherapyContentsQueryRepository psychotherapyContentsQueryRepository;
    private final DialogRepository dialogRepository;
    private final UserMentalStateRepository userMentalStateRepository;
    private final SmsUtil smsUtil;

    public String sendEmergencySms(String messageWithFlag, Long counselId) {
        String flag = messageWithFlag.trim().split("\\r\\n")[0];

        if (flag.equals("1")) {
            Counsel counsel = counselRepository.findById(counselId).get();
            User user = counsel.getUser();
            smsUtil.sendEmergencySms(user.getUserAgency().getPhoneNumber(), user.getName(), user.getPhoneNumber());
        }

        return messageWithFlag.trim().split("\\r\\n")[1];
    }

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

        return new StartCounselResponse(savedCounsel.getId(),
                "안녕하세요 " + user.getName() + "님! 어떤 내용이든 좋으니, 저에게 마음편히 이야기해주세요.");
    }

    @Transactional
    public CounselResultResponse endCounsel(Long counselId, String resultWithSummary) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        List<Disease> diseasesList = List.of(user.getDiseases());

        List<PsychotherapyContents> limitThreeContents = psychotherapyContentsQueryRepository
                .findRandomContentsByCategories(diseasesList, 3);

        int[] scores = getScores(resultWithSummary);
        int totalScore = Arrays.stream(scores).sum();

        saveNewUserMentalState(user, scores);

        String result = getResult(user, totalScore);
        counselRepository.findById(counselId).get().updateResult(result);
        String summary = resultWithSummary.trim().split("\\r\\n")[1];

        return new CounselResultResponse(result, summary, limitThreeContents);
    }

    @NotNull
    private static String getResult(User user, int totalScore) {
        String result = totalScore >= 0 ? CounselResult.GOOD.getKoreanResult() : CounselResult.BAD.getKoreanResult();
        result = user.getName() + result;
        return result;
    }

    private static int[] getScores(String resultWithSummary) {
        String[] scoreStringPart = resultWithSummary.trim().split("\\r\\n")[0].trim().split(",");
        return Arrays.stream(scoreStringPart).mapToInt(s -> Integer.parseInt(s.trim())).toArray();
    }

    private void saveNewUserMentalState(User user, int[] scores) {
        UserMentalState previousMentalState = userMentalStateRepository.findFirstByUserOrderByCreatedDateTimeDesc(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MENTAL_STATE));
        UserMentalState userMentalState = UserMentalState.toUserMentalStateEntity(user, previousMentalState, scores);
        userMentalStateRepository.save(userMentalState);
    }

    @Transactional
    public void validateBeforeEndCounsel(Long counselId) {
        Counsel counsel = validateBeforeChat(counselId);
        counsel.updateState(CounselState.FINISH_STATE);
    }

    public Counsel validateBeforeChat(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();
        return getNotClosedCounsel(counselId);
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

        CounselState counselState = CounselState.valueOfKoreanState(state);
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

    public FinishCounselResponse getFinishCounsel(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();
        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));
        if (counsel.getState() == CounselState.PROCEED_STATE) {
            throw new CommonException(ErrorCode.STILL_PROCEED_COUNSEL);
        }
        return new FinishCounselResponse(counsel, dialogRepository.findAllByCounselOrderByCreatedDateTimeAsc(counsel));
    }
}
