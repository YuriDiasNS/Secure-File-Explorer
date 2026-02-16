package com.yuridiasns.secure_file_explorer_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
import com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.response.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.response.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.filesystem.SecureFileSystem;
import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

@Service
public class FileExplorerService {

    private final Path rootPath;
    private final long maxDownloadSize;
    private final SecureFileSystem fs;

    public FileExplorerService(
            ExplorerProperties properties,
            SecureFileSystem fs) {

        this.rootPath = properties.getRootPath();
        this.maxDownloadSize = properties.getMaxDownloadSize();
        this.fs = fs;

        validateRoot();
    }

    private void validateRoot() {
        if (rootPath == null || !Files.isDirectory(rootPath)) {
            throw new IllegalStateException("Diretório raiz inválido");
        }

        if (Files.isSymbolicLink(rootPath)) {
            throw new IllegalStateException("Diretório raiz não pode ser symlink");
        }
    }

    public ExplorerResponse listRoot() {
        return new ExplorerResponse(
                fs.exploreTree(rootPath, "workdir"));
    }

    public FileInfoResponse info(String path) {
        Path target = PathSanitizer.sanitize(path, rootPath);
        PathSanitizer.rejectSymlinkInParents(rootPath, target);

        return fs.getInfo(rootPath, target);
    }

    public Resource loadAsResource(String path) {
        Path target = PathSanitizer.sanitize(path, rootPath);
        PathSanitizer.rejectAnySymlink(rootPath, target);

        return fs.loadFileForDownload(
                rootPath,
                target,
                maxDownloadSize);
    }
}