package com.yuridiasns.secure_file_explorer_backend.security;

import java.nio.file.Path;

public final class PathSanitizer {

    private PathSanitizer() {
        // Classe utilitária, não instanciável
    }

    public static Path sanitize(String userPath, Path rootPath) {

        // 1️⃣ Null safety
        if (userPath == null) {
            throw new IllegalArgumentException("Caminho inválido");
        }

        // 2️⃣ Bloqueia caminhos absolutos
        Path inputPath = Path.of(userPath);
        if (inputPath.isAbsolute()) {
            throw new IllegalArgumentException("Caminho absoluto não permitido");
        }

        // 3️⃣ Resolve o path dentro do root
        Path resolvedPath = rootPath
                .resolve(userPath)
                .normalize();

        // 4️⃣ Garante que ainda está dentro do root
        if (!resolvedPath.startsWith(rootPath)) {
            throw new IllegalArgumentException("Tentativa de acesso fora do diretório raiz");
        }

        return resolvedPath;
    }
}
