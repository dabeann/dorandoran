package com.backend.dorandoran.contents.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ContentsSwaggerConfig {

    @Bean
    GroupedOpenApi contentsDocs() {
        return GroupedOpenApi.builder()
                .group("콘텐츠 API")
                .packagesToScan("com.backend.dorandoran.contents.controller")
                .build();
    }
}
