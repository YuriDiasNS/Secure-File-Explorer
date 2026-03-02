package com.yuridiasns.secure_file_explorer_backend.external.manager;

import java.util.UUID;

public class DownloadJob {

    private final String jobId;
    private final String sourceUrl;

    private volatile String fileName;
    private volatile DownloadStatus status;
    private volatile int progress;
    private volatile String message;
    private volatile boolean cancelled = false;

    public DownloadJob(String sourceUrl) {
        this.jobId = UUID.randomUUID().toString();
        this.sourceUrl = sourceUrl;
        this.status = DownloadStatus.PENDING;
        this.progress = 0;
    }

    public String getJobId() {
        return jobId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public int getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
