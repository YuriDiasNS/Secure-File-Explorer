package com.yuridiasns.secure_file_explorer_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external-download")
public class ExternalDownloadProperties {

    private long maxSizeMb;
    private int maxConcurrent;
    private int connectTimeoutSeconds;
    private int readTimeoutSeconds;

    public long getMaxSizeMb() {
        return maxSizeMb;
    }

    public long getMaxSizeBytes() {
        return maxSizeMb * 1024 * 1024;
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public void setMaxSizeMb(long maxSizeMb) {
        this.maxSizeMb = maxSizeMb;
    }

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }
}