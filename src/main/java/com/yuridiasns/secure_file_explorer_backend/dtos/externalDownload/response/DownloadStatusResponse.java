package com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.response;

import com.yuridiasns.secure_file_explorer_backend.external.manager.DownloadStatus;

public class DownloadStatusResponse {

    private String jobId;
    private String fileName;
    private DownloadStatus status;
    private int progress;
    private String message;

    public DownloadStatusResponse(
            String jobId,
            String fileName,
            DownloadStatus status,
            int progress,
            String message) {

        this.jobId = jobId;
        this.fileName = fileName;
        this.status = status;
        this.progress = progress;
        this.message = message;
    }

    public String getJobId() {
        return jobId;
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
}
