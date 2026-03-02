package com.yuridiasns.secure_file_explorer_backend.filesystem;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.node.DirectoryNode;
import com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.node.FileNode;
import com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.response.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.exception.NotFoundException;
import com.yuridiasns.secure_file_explorer_backend.exception.SecurityViolationException;

@Service
public class DefaultSecureFileSystem implements SecureFileSystem {

    @Override
    public DirectoryNode exploreTree(Path directory, String logicalName) {
        DirectoryNode view = new DirectoryNode(logicalName);

        try (Stream<Path> entries = Files.list(directory)) {
            entries.forEach(path -> {
                String name = path.getFileName().toString();

                try {
                    if (Files.isSymbolicLink(path)) {
                        view.addChild(
                                DirectoryNode.symlink(name, "Symlink não pode ser navegado"));
                        return;
                    }

                    if (Files.isDirectory(path)) {
                        view.addChild(exploreTree(path, name));
                        return;
                    }

                    if (Files.isRegularFile(path)) {
                        view.addChild(new FileNode(name));
                    }

                } catch (Exception e) {
                    view.addChild(
                            FileNode.inaccessible(name, "Arquivo inacessível"));
                }
            });
        } catch (Exception e) {
            return DirectoryNode.inaccessible(
                    logicalName,
                    "Diretório inacessível");
        }

        return view;
    }

    @Override
    public FileInfoResponse getInfo(Path root, Path target) {

        if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new NotFoundException("Arquivo ou diretório não encontrado");
        }

        try {
            if (Files.isSymbolicLink(target)) {
                Path realTarget = target.toRealPath();
                boolean escapesRoot = !realTarget.startsWith(root.toRealPath());

                return new FileInfoResponse(
                        target.getFileName().toString(),
                        "symlink",
                        null,
                        0,
                        false,
                        escapesRoot);
            }

            boolean isDirectory = Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS);
            boolean isFile = Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS);

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
                    executable,
                    false);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao obter informações do arquivo", e);
        }
    }

    @Override
    public Resource loadFileForDownload(Path root, Path target, long maxSize) {

        try {
            Path realTarget = target.toRealPath();
            Path realRoot = root.toRealPath();

            if (!realTarget.startsWith(realRoot)) {
                throw new SecurityViolationException(
                        "Caminho resolve para fora do diretório permitido");
            }

            if (Files.isDirectory(realTarget)) {
                throw new BadRequestException(
                        "Não é possível fazer download de diretórios");
            }

            long size = Files.size(realTarget);
            if (size > maxSize) {
                throw new BadRequestException(
                        "Arquivo excede o tamanho máximo permitido para download");
            }

            Resource resource = new FileSystemResource(realTarget);

            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("Arquivo não pode ser lido");
            }

            return resource;

        } catch (SecurityViolationException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao preparar download do arquivo", e);
        }
    }
}