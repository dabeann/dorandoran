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
import com.backend.dorandoran.counsel.domain.request.DialogRequestResponse;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse;
import com.backend.dorandoran.counsel.domain.response.CounselResultResponse.CounselResultPsychotherapyContents;
import com.backend.dorandoran.counsel.repository.CounselRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.repository.UserRepository;
import java.util.ArrayList;
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

    public DialogRequestResponse startCounsel() {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        Optional<User> findUser = userRepository.findById(userId);
        User user = findUser.get();

        Counsel counsel = Counsel.builder()
                .user(user)
                .counselorType(CounselorType.COMMON_TYPE)
                .state(CounselState.PROCEED_STATE)
                .build();
        Counsel savedCounsel = counselRepository.save(counsel);

        // TODO 기본 멘트 바뀌면 바꾸기
        return new DialogRequestResponse(savedCounsel.getId(),
                "안녕하세요 " + user.getName() + "님! 어떤 이야기든 저에게 말해주세요.");
    }

    public CounselResultResponse endCounsel(String summary) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        Optional<User> findUser = userRepository.findById(userId);
        User user = findUser.get();

        List<Disease> diseasesList = Arrays.stream(user.getDiseases())
                .map(Disease::valueOf)
                .toList();

        List<PsychotherapyContents> contentsByCategories = psychotherapyContentsRepository
                .findAllByCategoryIn(diseasesList);
        Collections.shuffle(contentsByCategories);
        List<PsychotherapyContents> limitThreeContents = contentsByCategories.stream().limit(3).toList();

        List<CounselResultPsychotherapyContents> contents = new ArrayList<>();
        for (PsychotherapyContents content : limitThreeContents) {
            contents.add(new CounselResultPsychotherapyContents(
                    content.getTitle(),
                    content.getLink(),
                    content.getThumbnailLink()));
        }

        // TODO 심리 결과
        return new CounselResultResponse("result", summary, contents);
    }

    @Transactional
    public void validateBeforeEndCounsel(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();

        Optional<Counsel> findCounsel = counselRepository.findById(counselId);
        CommonValidator.notPresentOrThrow(findCounsel, ErrorCode.NOT_FOUND_COUNSEL);
        Counsel counsel = findCounsel.get();
        if (counsel.getState() == CounselState.FINISH_STATE) {
            throw new CommonException(ErrorCode.ALREADY_CLOSED_COUNSEL);
        }
        counsel.updateState(CounselState.FINISH_STATE);
    }

    public void validateBeforeChat(Long counselId) {
        UserInfoUtil.getUserIdOrThrow();
        Optional<Counsel> findCounsel = counselRepository.findById(counselId);
        CommonValidator.notPresentOrThrow(findCounsel, ErrorCode.NOT_FOUND_COUNSEL);
    }
}
