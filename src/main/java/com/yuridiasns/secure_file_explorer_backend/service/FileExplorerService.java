package com.yuridiasns.secure_file_explorer_backend.service;

import com.yuridiasns.secure_file_explorer_backend.config.ExplorerProperties;
import com.yuridiasns.secure_file_explorer_backend.model.DirectoryView;
import com.yuridiasns.secure_file_explorer_backend.model.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.model.FileView;

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
            throw new IllegalStateException(
                    "Diretório raiz inválido ou inexistente: " + rootPath.toAbsolutePath());
        }
    }

    // =========================
    // LISTAR ÁRVORE COMPLETA
    // =========================
    public ExplorerResponse list(String path) {
        DirectoryView rootView = buildDirectoryTree(rootPath, "");
        return new ExplorerResponse(rootView);
    }

    // =========================
    // MÉTODO RECURSIVO
    // =========================
    private DirectoryView buildDirectoryTree(Path directoryPath, String logicalName) {
        DirectoryView directoryView = new DirectoryView(logicalName);

        try {
            Files.list(directoryPath).forEach(path -> {
                String name = path.getFileName().toString();

                try {
                    if (Files.isDirectory(path)) {
                        directoryView.addChild(
                                buildDirectoryTree(path, name));
                    } else if (Files.isRegularFile(path)) {
                        directoryView.addChild(
                                new FileView(name));
                    }
                } catch (Exception e) {
                    directoryView.addChild(
                            FileView.inaccessible(name, "Arquivo inacessível"));
                }
            });
        } catch (Exception e) {
            return DirectoryView.inaccessible(
                    logicalName,
                    "Diretório inacessível");
        }

        return directoryView;
    }

    // =========================
    // AINDA NÃO IMPLEMENTADOS
    // =========================
    public Resource loadAsResource(String path) {
        throw new UnsupportedOperationException("Unimplemented method 'loadAsResource'");
    }

    public Object info(String path) {
        throw new UnsupportedOperationException("Unimplemented method 'info'");
    }
}
