package com.yuridiasns.secure_file_explorer_backend.filesystem;

public record UploadResult(
        String fileName,
        String relativePath,
        long size,
        boolean overwritten
) {}
