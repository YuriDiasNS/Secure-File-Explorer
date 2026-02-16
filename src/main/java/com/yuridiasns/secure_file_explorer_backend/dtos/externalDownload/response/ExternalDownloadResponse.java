package com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.response;

import java.util.List;

public class ExternalDownloadResponse {

    private List<String> jobIds;

    public ExternalDownloadResponse(List<String> jobIds) {
        this.jobIds = jobIds;
    }

    public List<String> getJobIds() {
        return jobIds;
    }
}
