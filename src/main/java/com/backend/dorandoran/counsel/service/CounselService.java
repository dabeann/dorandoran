package com.backend.dorandoran.counsel.service;

import com.backend.dorandoran.assessment.repository.UserMentalStateRepository;
import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.domain.counsel.CounselResult;
import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.common.domain.counsel.CounselorType;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.repository.PsychotherapyContentsRepository;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.response.CounselHistoryResponse;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse;
import com.backend.dorandoran.counsel.domain.response.FinishCounselResponse;
import com.backend.dorandoran.counsel.domain.response.ProceedCounselResponse;
import com.backend.dorandoran.counsel.domain.response.StartCounselResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CounselService {

    private final UserRepository userRepository;
    private final CounselRepository counselRepository;
    private final PsychotherapyContentsRepository psychotherapyContentsRepository;
    private final DialogRepository dialogRepository;
    private final UserMentalStateRepository userMentalStateRepository;
    private final SmsUtil smsUtil;

    public String sendEmergencySms(String messageWithFlag, Long counselId) {
        String flag = messageWithFlag.trim().split("\\r\\n")[0];

        // TODO flag가 1이면 sms 전송
        System.out.println("flag = " + flag);
        if (flag.equals("1")) {
            Counsel counsel = counselRepository.findById(counselId).get();
            User user = counsel.getUser();
            // TODO 내용 정해지면 수정
            //smsUtil.sendEmergencySms("보낼 전화번호", "내용내용");
        }

        return messageWithFlag.trim().split("\\r\\n")[1];
    }

    public StartCounselResponse startCounsel() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        Counsel counsel = Counsel.builder()
                .user(user)
                .counselorType(CounselorType.COMMON_TYPE)
                .state(CounselState.PROCEED_STATE)
                .build();
        Counsel savedCounsel = counselRepository.save(counsel);

        return new StartCounselResponse(savedCounsel.getId(),
                "안녕하세요 " + user.getName() + "님! 어떤 내용이든 좋으니, 저에게 마음편히 이야기해주세요.");
    }

    @Transactional
    public CounselResultResponse endCounsel(Long counselId, String resultWithSummary) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        User user = userRepository.findById(userId).get();

        List<Disease> diseasesList = List.of(user.getDiseases());

        List<PsychotherapyContents> contentsByCategories = psychotherapyContentsRepository
                .findAllByCategoryIn(diseasesList);
        Collections.shuffle(contentsByCategories);
        List<PsychotherapyContents> limitThreeContents = contentsByCategories.stream().limit(3).toList();

        String[] scoreStringPart = resultWithSummary.trim().split("\\r\\n")[0].trim().split(",");
        int[] scores = Arrays.stream(scoreStringPart).mapToInt(s -> Integer.parseInt(s.trim())).toArray();

        saveNewUserMentalState(user, scores);

        int totalScore = Arrays.stream(scores).sum();
        String result = totalScore >= 0 ? CounselResult.GOOD.getKoreanResult() : CounselResult.BAD.getKoreanResult();
        result = user.getName() + result;
        counselRepository.findById(counselId).get().updateResult(result);
        String summary = resultWithSummary.trim().split("\\r\\n")[1];
        return new CounselResultResponse(result, summary, limitThreeContents);
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
        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUNSEL));
        if (counsel.getState() == CounselState.FINISH_STATE) {
            throw new CommonException(ErrorCode.ALREADY_CLOSED_COUNSEL);
        }
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
