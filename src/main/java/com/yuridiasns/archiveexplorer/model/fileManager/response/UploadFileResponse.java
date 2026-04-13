package com.yuridiasns.archiveexplorer.model.fileManager.response;

public class UploadFileResponse {

    private String fileName;
    private String relativePath;
    private long size;
    private boolean overwritten;

    public UploadFileResponse(
            String fileName,
            String relativePath,
            long size,
            boolean overwritten) {

        this.fileName = fileName;
        this.relativePath = relativePath;
        this.size = size;
        this.overwritten = overwritten;
    }

    public String getFileName() {
        return fileName;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public long getSize() {
        return size;
    }

    public boolean isOverwritten() {
        return overwritten;
    }
}
