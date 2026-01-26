package com.yuridiasns.secure_file_explorer_backend.service;

import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.NotFoundException;
import com.yuridiasns.secure_file_explorer_backend.exception.SecurityViolationException;
import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.Response.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.Response.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.node.DirectoryNode;
import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.node.FileNode;
import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

@Service
public class FileExplorerService {

    private final Path rootPath;
    private final long maxDownloadSize;

    public FileExplorerService(ExplorerProperties properties) {
        this.rootPath = properties.getRootPath();
        this.maxDownloadSize = properties.getMaxDownloadSize();
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

    // =========================
    // LISTAR ROOT
    // =========================
    public ExplorerResponse listRoot() {
        DirectoryNode rootView = buildDirectoryTree(rootPath, "workdir");
        return new ExplorerResponse(rootView);
    }

    private DirectoryNode buildDirectoryTree(Path directoryPath, String logicalName) {
        DirectoryNode directoryView = new DirectoryNode(logicalName);

        try {
            Files.list(directoryPath).forEach(path -> {
                String name = path.getFileName().toString();

                try {
                    if (Files.isSymbolicLink(path)) {
                        directoryView.addChild(
                                DirectoryNode.symlink(name, "Symlink não pode ser navegado"));
                        return;
                    }

                    if (Files.isDirectory(path)) {
                        directoryView.addChild(buildDirectoryTree(path, name));
                        return;
                    }

                    if (Files.isRegularFile(path)) {
                        directoryView.addChild(new FileNode(name));
                    }

                } catch (Exception e) {
                    directoryView.addChild(
                            FileNode.inaccessible(name, "Arquivo inacessível"));
                }
            });
        } catch (Exception e) {
            return DirectoryNode.inaccessible(
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

        // Symlink NÃO pode estar em diretórios pais
        PathSanitizer.rejectSymlinkInParents(rootPath, target);

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

            // TODO: Talvez seja uma boa melhorar esse retorno ou até essa função inteira.
            // Preocupação principal: isDirectory ? "directory" : "file"
            return new FileInfoResponse(
                    target.getFileName().toString(),
                    isDirectory ? "directory" : "file",
                    mimeType,
                    size,
                    executable,
                    false);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao obter informações do arquivo", e);
        }
    }

    // =========================
    // DOWNLOAD
    // =========================
    public Resource loadAsResource(String path) {

        Path logicalPath = PathSanitizer.sanitize(path, rootPath);

        // Bloqueia QUALQUER symlink
        PathSanitizer.rejectAnySymlink(rootPath, logicalPath);

        if (!Files.exists(logicalPath, LinkOption.NOFOLLOW_LINKS)) {
            throw new NotFoundException("Arquivo não encontrado");
        }

        if (Files.isDirectory(logicalPath, LinkOption.NOFOLLOW_LINKS)) {
            throw new BadRequestException(
                    "Não é possível fazer download de diretórios");
        }

        try {
            Path realTarget = logicalPath.toRealPath();
            Path realRoot = rootPath.toRealPath();

            if (!realTarget.startsWith(realRoot)) {
                throw new SecurityViolationException(
                        "Caminho resolve para fora do diretório permitido");
            }

            Resource resource = new FileSystemResource(realTarget);

            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("Arquivo não pode ser lido");
            }

            long fileSize = Files.size(realTarget);

            if (fileSize > maxDownloadSize) {
                throw new BadRequestException(
                        "Arquivo excede o tamanho máximo permitido para download");
            }

            return resource;

        } catch (SecurityViolationException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao preparar download do arquivo", e);
        }
    }

}
