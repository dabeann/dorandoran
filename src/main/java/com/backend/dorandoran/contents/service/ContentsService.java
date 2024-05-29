package com.backend.dorandoran.contents.service;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.validator.CommonValidator;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import com.backend.dorandoran.contents.domain.entity.Quotation;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ContentsService {

    private final UserRepository userRepository;
    private final QuotationRepository quotationRepository;
    private final PsychotherapyContentsRepository psychotherapyContentsRepository;

    @Transactional
    public void updateTodayQuotation() {
        List<User> usersWithDiseases = userRepository.findAllByDiseasesNotNull();
        for (User user : usersWithDiseases) {
            List<Quotation> quotationsByDiseases = quotationRepository.findAllByCategoryIn(Arrays.stream(user.getDiseases())
                    .map(Disease::valueOf)
                    .toList());
            Collections.shuffle(quotationsByDiseases);
            user.updateTodayQuotation(quotationsByDiseases.stream().findFirst().get());
        }
    }

    public ContentsResponse getMainContents(String category) {
        final Long userId = UserInfoUtil.getUserIdOrThrow();
        Optional<User> findUser = userRepository.findById(userId);
        User user = findUser.get();

        String quotation = null;
        if (user.getDiseases() != null) {
            quotation = user.getTodayQuotation().getContents();
        }

        List<PsychotherapyContents> psychotherapyContentsList;
        if (user.getDiseases() == null && category == null) {
            // 심리검사 X
            psychotherapyContentsList = getPsychotherapyContentsListWithCategory("우울증");
        } else if (category == null) {
            // "당신을 위한 콘텐츠"
            List<Disease> diseasesList = Arrays.stream(user.getDiseases())
                    .map(Disease::valueOf)
                    .toList();
            psychotherapyContentsList = getPersonalizedPsychotherapyContentsList(diseasesList);
        } else {
            psychotherapyContentsList = getPsychotherapyContentsListWithCategory(category);
        }

        return new ContentsResponse(quotation, psychotherapyContentsList);
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