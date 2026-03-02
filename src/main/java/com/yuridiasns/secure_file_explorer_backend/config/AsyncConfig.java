package com.yuridiasns.secure_file_explorer_backend.config;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private final ExternalDownloadProperties properties;

    public AsyncConfig(ExternalDownloadProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "downloadExecutor")
    public Executor downloadExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getMaxConcurrent());
        executor.setMaxPoolSize(properties.getMaxConcurrent());
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("download-");
        executor.initialize();

        return executor;
    }
}