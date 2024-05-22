package com.backend.dorandoran.user.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserSwaggerConfig {

    @Bean
    GroupedOpenApi userDocs() {
        return GroupedOpenApi.builder()
                .group("User API")
                .packagesToScan("com.backend.dorandoran.user.controller")
                .build();
    }
}
