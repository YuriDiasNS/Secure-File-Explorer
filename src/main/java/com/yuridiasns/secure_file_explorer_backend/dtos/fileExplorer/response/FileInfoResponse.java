package com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.response;

public class FileInfoResponse {

    private String name;
    private String type; // file | directory | symlink
    private String mimeType;
    private long size;
    private boolean executable;
    private boolean pointsOutsideRoot;

    public FileInfoResponse(
            String name,
            String type,
            String mimeType,
            long size,
            boolean executable,
            boolean pointsOutsideRoot
    ) {
        this.name = name;
        this.type = type;
        this.mimeType = mimeType;
        this.size = size;
        this.executable = executable;
        this.pointsOutsideRoot = pointsOutsideRoot;
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
    public boolean isPointsOutsideRoot() {
        return pointsOutsideRoot;
    }
}
