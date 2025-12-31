package com.yuridiasns.secure_file_explorer_backend.security;

import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.SecurityViolationException;

import java.nio.file.Path;

public final class PathSanitizer {

    private PathSanitizer() {}

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
            throw new SecurityViolationException("Tentativa de acesso fora do diretório raiz");
        }

        return resolvedPath;
    }
}
