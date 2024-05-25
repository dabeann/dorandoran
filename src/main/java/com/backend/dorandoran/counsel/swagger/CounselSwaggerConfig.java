package com.backend.dorandoran.counsel.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CounselSwaggerConfig {

    @Bean
    GroupedOpenApi counselDocs() {
        return GroupedOpenApi.builder()
                .group("Counsel API")
                .packagesToScan("com.backend.dorandoran.counsel.controller")
                .build();
    }
}
