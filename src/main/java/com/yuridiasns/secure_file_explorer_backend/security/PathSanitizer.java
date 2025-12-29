package com.yuridiasns.secure_file_explorer_backend.security;

import java.nio.file.Path;

public final class PathSanitizer {

    private PathSanitizer() {
    }

    public static Path sanitize(String userPath, Path rootPath) {

        if (userPath == null || userPath.isBlank()) {
            throw new IllegalArgumentException("Caminho inválido");
        }

        Path inputPath = Path.of(userPath);

        if (inputPath.isAbsolute()) {
            throw new IllegalArgumentException("Caminho absoluto não permitido");
        }

        Path resolvedPath = rootPath
                .resolve(userPath)
                .normalize();

        if (!resolvedPath.startsWith(rootPath)) {
            throw new IllegalArgumentException("Tentativa de acesso fora do diretório raiz");
        }

        return resolvedPath;
    }
}
