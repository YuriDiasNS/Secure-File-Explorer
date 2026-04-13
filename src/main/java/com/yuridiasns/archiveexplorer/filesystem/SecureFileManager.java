package com.yuridiasns.archiveexplorer.filesystem;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface SecureFileManager {

    void delete(Path root, Path target);

    UploadResult upload(
            Path root,
            Path targetDir,
            MultipartFile file);
}
