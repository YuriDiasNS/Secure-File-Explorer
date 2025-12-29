package com.yuridiasns.secure_file_explorer_backend.model;

public class FileInfoResponse {

    private String name;
    private String type;          // file | directory
    private String mimeType;      // application/pdf, application/x-msdownload, etc
    private long size;            // bytes
    private boolean executable;

    public FileInfoResponse(
            String name,
            String type,
            String mimeType,
            long size,
            boolean executable
    ) {
        this.name = name;
        this.type = type;
        this.mimeType = mimeType;
        this.size = size;
        this.executable = executable;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return size;
    }

    public boolean isExecutable() {
        return executable;
    }
}
