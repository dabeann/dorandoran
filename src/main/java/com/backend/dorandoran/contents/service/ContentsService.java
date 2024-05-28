package com.backend.dorandoran.contents.service;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.domain.response.ContentsResponse;
import com.backend.dorandoran.contents.repository.PsychotherapyContentsRepository;
import com.backend.dorandoran.contents.repository.QuotationRepository;
import com.backend.dorandoran.security.service.UserInfoUtil;
import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ContentsService {

    private final UserRepository userRepository;
    private final QuotationRepository quotationRepository;
    private final PsychotherapyContentsRepository psychotherapyContentsRepository;

    public ContentsResponse getMainContents(String category) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        Optional<User> findUser = userRepository.findById(userId);
        User user = findUser.get();

        List<Disease> diseasesList = Arrays.stream(user.getDiseases())
                .map(Disease::valueOf)
                .toList();

        List<PsychotherapyContents> psychotherapyContentsList;
        if (category == null) {
            // null이면 "당신을 위한 콘텐츠"
            psychotherapyContentsList = getPersonalizedPsychotherapyContentsList(diseasesList);
        } else {
            psychotherapyContentsList = getPsychotherapyContentsListWithCategory(category);
        }

        // TODO 하루에 하나 병에 따라 랜덤 출력
        return new ContentsResponse("quotation", psychotherapyContentsList);
    }

    private List<PsychotherapyContents> getPsychotherapyContentsListWithCategory(String category) {
        Disease disease = Disease.valueOfKoreanName(category);
        CommonValidator.notNullOrThrow(disease, ErrorCode.NOT_FOUND_DISEASE);
        return psychotherapyContentsRepository.findAllByCategory(disease);
    }

    private List<PsychotherapyContents> getPersonalizedPsychotherapyContentsList(List<Disease> diseaseList) {
        List<PsychotherapyContents> contentsByCategories = psychotherapyContentsRepository
                .findAllByCategoryIn(diseaseList);
        Collections.shuffle(contentsByCategories);
        return contentsByCategories.stream().limit(5).toList();
    }
}