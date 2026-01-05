package com.yuridiasns.secure_file_explorer_backend.service;

import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
//import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.NotFoundException;
import com.yuridiasns.secure_file_explorer_backend.model.Response.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.model.Response.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.model.View.DirectoryView;
import com.yuridiasns.secure_file_explorer_backend.model.View.FileView;
import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

@Service
public class FileExplorerService {

    private final Path rootPath;

    public FileExplorerService(ExplorerProperties properties) {
        this.rootPath = properties.getRootPath();
        validateRoot();
    }

    private void validateRoot() {
        if (rootPath == null || !Files.isDirectory(rootPath)) {
            throw new IllegalStateException("Diretório raiz inválido");
        }
    }

    // =========================
    // LISTAR ROOT
    // =========================
    public ExplorerResponse listRoot() {
        DirectoryView rootView = buildDirectoryTree(rootPath, "workdir");
        return new ExplorerResponse(rootView);
    }

    private DirectoryView buildDirectoryTree(Path directoryPath, String logicalName) {
        DirectoryView directoryView = new DirectoryView(logicalName);

        try {
            Files.list(directoryPath).forEach(path -> {
                String name = path.getFileName().toString();

                try {
                    if (Files.isSymbolicLink(path)) {
                        directoryView.addChild(
                                DirectoryView.symlink(name, "Symlink não pode ser navegado"));
                        return;
                    }

                    if (Files.isDirectory(path)) {
                        directoryView.addChild(buildDirectoryTree(path, name));
                        return;
                    }

                    if (Files.isRegularFile(path)) {
                        directoryView.addChild(new FileView(name));
                    }

                } catch (Exception e) {
                    directoryView.addChild(
                            FileView.inaccessible(name, "Arquivo inacessível"));
                }
            });
        } catch (Exception e) {
            return DirectoryView.inaccessible(
                    logicalName,
                    "Diretório inacessível");
        }

        return directoryView;
    }

    // =========================
    // INFO
    // =========================
    public FileInfoResponse info(String path) {

        Path target = PathSanitizer.sanitize(path, rootPath);

        if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new NotFoundException("Arquivo ou diretório não encontrado");
        }

        try {
            if (Files.isSymbolicLink(target)) {

                Path realTarget = target.toRealPath();
                boolean escapesRoot = !realTarget.startsWith(rootPath.toRealPath());

                return new FileInfoResponse(
                        target.getFileName().toString(),
                        "symlink",
                        null,
                        0,
                        false,
                        escapesRoot);
            }

            boolean isDirectory = Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS);
            boolean isFile = Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS);

            String mimeType = null;
            long size = 0;
            boolean executable = false;

            if (isFile) {
                mimeType = Files.probeContentType(target);
                size = Files.size(target);
                executable = Files.isExecutable(target);
            }

            return new FileInfoResponse(
                    target.getFileName().toString(),
                    isDirectory ? "directory" : "file",
                    mimeType,
                    size,
                    executable,
                    false);

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao obter informações do arquivo", e);
        }
    }

    // =========================
    // DOWNLOAD (futuro)
    // =========================
    public Resource loadAsResource(String path) {
        throw new UnsupportedOperationException("Download ainda não implementado");
    }
}
