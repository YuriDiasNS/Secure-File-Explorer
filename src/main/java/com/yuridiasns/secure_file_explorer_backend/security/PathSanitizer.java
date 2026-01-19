package com.yuridiasns.secure_file_explorer_backend.security;

import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.SecurityViolationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public final class PathSanitizer {

    private PathSanitizer() {
    }

    // =========================
    // SANITIZE BASE
    // =========================
    public static Path sanitize(String userPath, Path rootPath) {

        if (userPath == null || userPath.isBlank()) {
            throw new BadRequestException("O parâmetro 'path' é obrigatório");
        }

        Path inputPath = Path.of(userPath);

        if (inputPath.isAbsolute()) {
            throw new SecurityViolationException("Caminho absoluto não permitido");
        }

        Path resolvedPath = rootPath
                .resolve(userPath)
                .normalize();

        if (!resolvedPath.startsWith(rootPath)) {
            throw new SecurityViolationException(
                    "Tentativa de acesso fora do diretório raiz");
        }

        return resolvedPath;
    }

    // =========================
    // bloqueia symlink APENAS nos pais
    // =========================
    public static void rejectSymlinkInParents(Path root, Path target) {
        try {
            Path current = root.toRealPath(LinkOption.NOFOLLOW_LINKS);

            Path relative = root.relativize(target);

            for (int i = 0; i < relative.getNameCount() - 1; i++) {
                current = current.resolve(relative.getName(i));

                if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                        && Files.isSymbolicLink(current)) {
                    throw new SecurityViolationException(
                            "Caminho inválido: symlink em diretório pai (" +
                                    relative.getName(i) + ")");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao validar symlinks no caminho", e);
        }
    }

    // =========================
    // bloqueia QUALQUER symlink
    // =========================
    public static void rejectAnySymlink(Path root, Path target) {
        try {
            Path current = root.toRealPath(LinkOption.NOFOLLOW_LINKS);

            for (Path part : root.relativize(target)) {
                current = current.resolve(part);

                if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                        && Files.isSymbolicLink(current)) {
                    throw new SecurityViolationException(
                            "Download bloqueado: symlink detectado (" + part + ")");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao validar symlinks no caminho", e);
        }
    }

    // =========================
    // garante escopo absoluto (runtime)
    // =========================
    public static void ensureWithinRoot(Path root, Path candidate) {
        if (!candidate.normalize().startsWith(root)) {
            throw new SecurityViolationException(
                    "Tentativa de acesso fora do diretório permitido");
        }
    }
}
