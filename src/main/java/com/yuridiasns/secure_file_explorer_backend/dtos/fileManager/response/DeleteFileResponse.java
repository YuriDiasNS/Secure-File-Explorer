package com.yuridiasns.secure_file_explorer_backend.dtos.fileManager.response;

import java.time.Instant;

public class DeleteFileResponse {

    private String path;
    private boolean deleted;
    private Instant timestamp;

    public DeleteFileResponse(String path, boolean deleted) {
        this.path = path;
        this.deleted = deleted;
        this.timestamp = Instant.now();
    }

    public String getPath() {
        return path;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
