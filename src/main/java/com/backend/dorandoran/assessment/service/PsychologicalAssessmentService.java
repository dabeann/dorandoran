package com.backend.dorandoran.assessment.service;

import com.backend.dorandoran.assessment.repository.PsychologicalAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PsychologicalAssessmentService {

    private final PsychologicalAssessmentRepository repository;

    public Boolean hasPsychologicalAssessmentResult() {
        return null;
    }
}
