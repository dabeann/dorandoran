package com.backend.dorandoran.assessment.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PsychologicalAssessmentSwaggerConfig {

    @Bean
    GroupedOpenApi assessmentDocs() {
        return GroupedOpenApi.builder()
                .group("심리검사 API")
                .packagesToScan("com.backend.dorandoran.assessment.controller")
                .build();
    }
}
