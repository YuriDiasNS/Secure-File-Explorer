package com.yuridiasns.secure_file_explorer_backend.service;

import com.yuridiasns.secure_file_explorer_backend.model.fileManager.request.DeleteFileRequest;
import com.yuridiasns.secure_file_explorer_backend.model.fileManager.response.DeleteFileResponse;
import com.yuridiasns.secure_file_explorer_backend.model.fileManager.response.UploadFileResponse;
import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.NotFoundException;
import com.yuridiasns.secure_file_explorer_backend.exception.SecurityViolationException;
import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

@Service
public class FileManagerService {

    private final Path rootPath;

    public FileManagerService(ExplorerProperties properties) {
        this.rootPath = properties.getRootPath();
    }

    // =========================
    // DELETE
    // =========================
    public DeleteFileResponse delete(DeleteFileRequest request) {

        deleteInternal(request.getPath());

        return new DeleteFileResponse(
                request.getPath(),
                true);
    }

    private void deleteInternal(String path) {

        Path target = PathSanitizer.sanitize(path, rootPath);

        if (target.equals(rootPath)) {
            throw new BadRequestException("Não é permitido deletar o diretório raiz");
        }

        PathSanitizer.rejectSymlinkInParents(rootPath, target);

        if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new NotFoundException("Arquivo ou diretório não encontrado");
        }

        try {
            if (Files.isSymbolicLink(target)) {
                Files.delete(target);
                return;
            }

            if (Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
                deleteDirectoryRecursively(target);
                return;
            }

            Files.delete(target);

        } catch (AccessDeniedException e) {
            throw new BadRequestException(
                    "Arquivo está em uso ou bloqueado pelo sistema");

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao remover arquivo", e);
        }
    }

    private void deleteDirectoryRecursively(Path directory) throws IOException {

        Files.walkFileTree(
                directory,
                EnumSet.noneOf(FileVisitOption.class), // NÃO segue symlinks
                Integer.MAX_VALUE,
                new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException {

                        PathSanitizer.ensureWithinRoot(rootPath, dir);

                        if (Files.isSymbolicLink(dir)) {
                            Files.delete(dir);
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {

                        PathSanitizer.ensureWithinRoot(rootPath, file);

                        if (Files.isSymbolicLink(file)) {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                            throws IOException {

                        if (exc != null) {
                            throw exc;
                        }

                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    // =========================
    // UPLOAD 
    // =========================
    // TODO: Funciona, mas assim como o controller seria uma boa ideia refinar isso depois
    public UploadFileResponse upload(MultipartFile file, String path) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Arquivo não informado ou vazio");
        }

        String originalFileName = Path.of(file.getOriginalFilename())
                .getFileName()
                .toString();

        if (originalFileName.isBlank()) {
            throw new BadRequestException("Nome do arquivo inválido");
        }

        Path targetDir = (path == null || path.isBlank())
                ? rootPath
                : PathSanitizer.sanitize(path, rootPath);

        // Bloqueia symlink nos pais
        PathSanitizer.rejectSymlinkInParents(rootPath, targetDir);

        try {
            if (!Files.exists(targetDir, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(targetDir);
            }

            if (!Files.isDirectory(targetDir, LinkOption.NOFOLLOW_LINKS)) {
                throw new BadRequestException("Destino não é um diretório");
            }

            Path targetFile = targetDir
                    .resolve(originalFileName)
                    .normalize();

            if (!targetFile.startsWith(rootPath)) {
                throw new SecurityViolationException(
                        "Tentativa de escrita fora do diretório permitido");
            }

            boolean overwritten = Files.exists(
                    targetFile, LinkOption.NOFOLLOW_LINKS);

            Files.copy(
                    file.getInputStream(),
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING);

            String relativePath = rootPath
                    .relativize(targetFile)
                    .toString();

            return new UploadFileResponse(
                    originalFileName,
                    relativePath,
                    file.getSize(),
                    overwritten);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao salvar o arquivo no sistema", e);
        }
    }

}
