package com.yuridiasns.archiveexplorer.filesystem;

public record UploadResult(
        String fileName,
        String relativePath,
        long size,
        boolean overwritten
) {}
