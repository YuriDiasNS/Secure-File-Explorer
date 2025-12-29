package com.yuridiasns.secure_file_explorer_backend.service;

import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
import com.yuridiasns.secure_file_explorer_backend.model.DirectoryView;
import com.yuridiasns.secure_file_explorer_backend.model.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.model.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.model.FileView;
import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
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
            throw new IllegalStateException("Diretório raiz inválido ou inexistente: " + rootPath.toAbsolutePath());
        }
    }

    // LISTAR ÁRVORE COMPLETA

    public ExplorerResponse list(String path) {
        // TODO: Implementar navegação por subdiretórios
        // Aqui o path está sendo recebido do endpoint, porem como atualmente não
        // e utilizado para navegar, ele é ignorado, logo sempre será listados os
        // diretorios
        // a partir do diretorio raiz.
        DirectoryView rootView = buildDirectoryTree(rootPath, "workdir");
        return new ExplorerResponse(rootView);
    }

    private DirectoryView buildDirectoryTree(Path directoryPath, String logicalName) {
        DirectoryView directoryView = new DirectoryView(logicalName);

        try {
            Files.list(directoryPath).forEach(path -> {
                String name = path.getFileName().toString();
                try {
                    
                    // BLOQUEIA SYMLINK ANTES DE TUDO
                    if (Files.isSymbolicLink(path)) {
                        directoryView.addChild(DirectoryView.symlink(name,"Symlink não pode ser navegado"));
                        return;
                    }

                    // DIRETÓRIO REAL
                    if (Files.isDirectory(path)) {
                        directoryView.addChild(buildDirectoryTree(path, name));
                        return;
                    }
                    
                    // ARQUIVO REAL
                    if (Files.isRegularFile(path)) {
                        directoryView.addChild(new FileView(name));
                    }
                } catch (Exception e) {
                    directoryView.addChild(FileView.inaccessible(name,"Arquivo inacessível"));
                }
            });
        } catch (Exception e) {
            return DirectoryView.inaccessible(
                    logicalName,
                    "Diretório inacessível");
        }
        return directoryView;
    }

    // NÃO IMPLEMENTADOS

    public Resource loadAsResource(String path) {
        // TODO: Implementar download de arquivo
        // Path safePath = PathSanitizer.sanitize(path, rootPath);
        throw new UnsupportedOperationException("Unimplemented method 'loadAsResource'");
    }

    public FileInfoResponse info(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Caminho não informado");
        }

        Path target = PathSanitizer.sanitize(path, rootPath);

        if (!Files.exists(target)) {
            throw new IllegalArgumentException("Arquivo ou diretório não encontrado");
        }
        try {
            boolean isDirectory = Files.isDirectory(target);
            boolean isFile = Files.isRegularFile(target);

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
                    executable);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao obter informações do arquivo", e);
        }
    }
}
