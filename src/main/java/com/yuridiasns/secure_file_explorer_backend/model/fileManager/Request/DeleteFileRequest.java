package com.yuridiasns.secure_file_explorer_backend.model.fileManager.request;

import jakarta.validation.constraints.NotBlank;

public class DeleteFileRequest {

    @NotBlank(message = "O campo 'path' é obrigatório")
    private String path;

    public DeleteFileRequest() {
    }

    public DeleteFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
