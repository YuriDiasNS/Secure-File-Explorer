package com.yuridiasns.archiveexplorer.filesystem;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yuridiasns.archiveexplorer.exception.BadRequestException;
import com.yuridiasns.archiveexplorer.exception.SecurityViolationException;
import com.yuridiasns.archiveexplorer.security.PathSanitizer;

@Service
public class DefaultSecureFileManager implements SecureFileManager {

    @Override
    public void delete(Path root, Path target) {

        try {
            if (Files.isSymbolicLink(target)) {
                Files.delete(target);
                return;
            }

            if (Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
                deleteDirectoryRecursively(root, target);
                return;
            }

            Files.delete(target);

        } catch (AccessDeniedException e) {
            throw new BadRequestException(
                    "Arquivo está em uso ou bloqueado pelo sistema");

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao remover arquivo", e);
        }
    }

    private void deleteDirectoryRecursively(Path root, Path directory)
            throws IOException {

        Files.walkFileTree(
                directory,
                EnumSet.noneOf(FileVisitOption.class),
                Integer.MAX_VALUE,
                new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(
                            Path dir,
                            BasicFileAttributes attrs) throws IOException {

                        PathSanitizer.ensureWithinRoot(root, dir);

                        if (Files.isSymbolicLink(dir)) {
                            Files.delete(dir);
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attrs) throws IOException {

                        PathSanitizer.ensureWithinRoot(root, file);
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir,
                            IOException exc) throws IOException {

                        if (exc != null) throw exc;
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    @Override
    public UploadResult upload(
            Path root,
            Path targetDir,
            MultipartFile file) {

        try {
            if (!Files.exists(targetDir, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(targetDir);
            }

            if (!Files.isDirectory(targetDir, LinkOption.NOFOLLOW_LINKS)) {
                throw new BadRequestException(
                        "Destino não é um diretório");
            }

            Path targetFile = targetDir
                    .resolve(file.getOriginalFilename())
                    .normalize();

            if (!targetFile.startsWith(root)) {
                throw new SecurityViolationException(
                        "Tentativa de escrita fora do diretório permitido");
            }

            boolean overwritten = Files.exists(
                    targetFile, LinkOption.NOFOLLOW_LINKS);

            Files.copy(
                    file.getInputStream(),
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING);

            return new UploadResult(
                    targetFile.getFileName().toString(),
                    root.relativize(targetFile).toString(),
                    file.getSize(),
                    overwritten);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao salvar arquivo", e);
        }
    }
}
