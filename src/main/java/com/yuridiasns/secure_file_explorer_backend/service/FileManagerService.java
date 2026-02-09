package com.yuridiasns.secure_file_explorer_backend.service;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.NotFoundException;
import com.yuridiasns.secure_file_explorer_backend.filesystem.SecureFileManager;
import com.yuridiasns.secure_file_explorer_backend.filesystem.UploadResult;
import com.yuridiasns.secure_file_explorer_backend.model.fileManager.request.DeleteFileRequest;
import com.yuridiasns.secure_file_explorer_backend.model.fileManager.response.DeleteFileResponse;
import com.yuridiasns.secure_file_explorer_backend.model.fileManager.response.UploadFileResponse;
import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

@Service
public class FileManagerService {

    private final Path rootPath;
    private final SecureFileManager fs;

    public FileManagerService(
            ExplorerProperties properties,
            SecureFileManager fs) {

        this.rootPath = properties.getRootPath();
        this.fs = fs;
    }

    // =========================
    // DELETE
    // =========================
    public DeleteFileResponse delete(DeleteFileRequest request) {

        Path target = PathSanitizer.sanitize(
                request.getPath(), rootPath);

        if (target.equals(rootPath)) {
            throw new BadRequestException(
                    "Não é permitido deletar o diretório raiz");
        }

        PathSanitizer.rejectSymlinkInParents(rootPath, target);

        if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new NotFoundException(
                    "Arquivo ou diretório não encontrado");
        }

        fs.delete(rootPath, target);

        return new DeleteFileResponse(
                request.getPath(), true);
    }

    // =========================
    // UPLOAD
    // =========================
    public UploadFileResponse upload(
            MultipartFile file,
            String path) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "Arquivo não informado ou vazio");
        }

        String filename = Path.of(file.getOriginalFilename())
                .getFileName()
                .toString();

        if (filename.isBlank()) {
            throw new BadRequestException(
                    "Nome do arquivo inválido");
        }

        Path targetDir = (path == null || path.isBlank())
                ? rootPath
                : PathSanitizer.sanitize(path, rootPath);

        PathSanitizer.rejectSymlinkInParents(
                rootPath, targetDir);

        UploadResult result =
                fs.upload(rootPath, targetDir, file);

        return new UploadFileResponse(
                result.fileName(),
                result.relativePath(),
                result.size(),
                result.overwritten());
    }
}
