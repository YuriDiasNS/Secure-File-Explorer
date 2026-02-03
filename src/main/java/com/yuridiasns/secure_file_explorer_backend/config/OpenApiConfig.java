package com.yuridiasns.secure_file_explorer_backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("secure-file-explorer")
                .pathsToMatch("/api/**")
                .build();
    }
}
