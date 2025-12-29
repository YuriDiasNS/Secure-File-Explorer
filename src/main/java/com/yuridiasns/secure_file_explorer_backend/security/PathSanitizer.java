package com.yuridiasns.secure_file_explorer_backend.security;

import java.nio.file.Path;

public final class PathSanitizer {

    private PathSanitizer() {}

    public static Path sanitize(String userPath, Path rootPath) {

        if (userPath == null || userPath.isBlank()) {
            throw new IllegalArgumentException("Caminho inválido");
        }

        Path inputPath = Path.of(userPath);

        // 1️⃣ Bloqueia path absoluto
        if (inputPath.isAbsolute()) {
            throw new IllegalArgumentException("Caminho absoluto não permitido");
        }

        try {
            // 2️⃣ Resolve dentro do root
            Path resolvedPath = rootPath
                    .resolve(userPath)
                    .normalize();

            // 3️⃣ Garante que ainda parece estar dentro do root
            if (!resolvedPath.startsWith(rootPath)) {
                throw new IllegalArgumentException("Tentativa de acesso fora do diretório raiz");
            }

            // 4️⃣ Resolve symlinks (caminho REAL)
            Path realPath = resolvedPath.toRealPath();

            // 5️⃣ Valida novamente após seguir symlink
            // TODO: Lembrar de apagar a pasta de teste com symlink criada C:\symlink-test-target
            if (!realPath.startsWith(rootPath.toRealPath())) {
                throw new IllegalArgumentException("Symlink aponta para fora do diretório permitido");
            }

            return realPath;

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalArgumentException("Caminho inválido ou inacessível");
        }
    }
}
