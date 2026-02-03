// package com.yuridiasns.secure_file_explorer_backend.filesystem;

// import java.nio.file.Files;
// import java.nio.file.LinkOption;
// import java.nio.file.Path;

// import org.springframework.core.io.FileSystemResource;
// import org.springframework.core.io.Resource;
// import org.springframework.stereotype.Service;

// import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
// import com.yuridiasns.secure_file_explorer_backend.exception.NotFoundException;
// import com.yuridiasns.secure_file_explorer_backend.exception.SecurityViolationException;
// import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.node.DirectoryNode;
// import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.node.FileNode;
// import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.response.FileInfoResponse;
// import com.yuridiasns.secure_file_explorer_backend.security.PathSanitizer;

// @Service
// public class DefaultSecureFileSystem implements SecureFileSystem {

//     @Override
//     public DirectoryNode exploreTree(Path root, String logicalName) {
//         DirectoryNode directoryView = new DirectoryNode(logicalName);

//         try {
//             Files.list(root).forEach(path -> {
//                 String name = path.getFileName().toString();

//                 try {
//                     if (Files.isSymbolicLink(path)) {
//                         directoryView.addChild(
//                                 DirectoryNode.symlink(name, "Symlink não pode ser navegado"));
//                         return;
//                     }

//                     if (Files.isDirectory(path)) {
//                         directoryView.addChild(exploreTree(path, name));
//                         return;
//                     }

//                     if (Files.isRegularFile(path)) {
//                         directoryView.addChild(new FileNode(name));
//                     }

//                 } catch (Exception e) {
//                     directoryView.addChild(
//                             FileNode.inaccessible(name, "Arquivo inacessível"));
//                 }
//             });
//         } catch (Exception e) {
//             return DirectoryNode.inaccessible(
//                     logicalName,
//                     "Diretório inacessível");
//         }

//         return directoryView;
//     }

//     @Override
//     public FileInfoResponse getInfo(Path root, Path target) {

//         if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
//             throw new NotFoundException("Arquivo ou diretório não encontrado");
//         }

//         try {
//             if (Files.isSymbolicLink(target)) {

//                 Path realTarget = target.toRealPath();
//                 boolean escapesRoot = !realTarget.startsWith(root.toRealPath());

//                 return new FileInfoResponse(
//                         target.getFileName().toString(),
//                         "symlink",
//                         null,
//                         0,
//                         false,
//                         escapesRoot);
//             }

//             boolean isDirectory = Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS);
//             boolean isFile = Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS);

//             String mimeType = null;
//             long size = 0;
//             boolean executable = false;

//             if (isFile) {
//                 mimeType = Files.probeContentType(target);
//                 size = Files.size(target);
//                 executable = Files.isExecutable(target);
//             }

//             // TODO: Talvez seja uma boa melhorar esse retorno ou até essa função inteira.
//             // Preocupação principal: isDirectory ? "directory" : "file"
//             return new FileInfoResponse(
//                     target.getFileName().toString(),
//                     isDirectory ? "directory" : "file",
//                     mimeType,
//                     size,
//                     executable,
//                     false);

//         } catch (Exception e) {
//             throw new IllegalStateException(
//                     "Erro ao obter informações do arquivo", e);
//         }
//     }

//     @Override
//     public Resource loadFileForDownload(Path root, String target, long maxSize) {
//         Path logicalPath = PathSanitizer.sanitize(target, root);

//         // Bloqueia QUALQUER symlink
//         PathSanitizer.rejectAnySymlink(root, logicalPath);

//         if (!Files.exists(logicalPath, LinkOption.NOFOLLOW_LINKS)) {
//             throw new NotFoundException("Arquivo não encontrado");
//         }

//         if (Files.isDirectory(logicalPath, LinkOption.NOFOLLOW_LINKS)) {
//             throw new BadRequestException(
//                     "Não é possível fazer download de diretórios");
//         }

//         try {
//             Path realTarget = logicalPath.toRealPath();
//             Path realRoot = root.toRealPath();

//             if (!realTarget.startsWith(realRoot)) {
//                 throw new SecurityViolationException(
//                         "Caminho resolve para fora do diretório permitido");
//             }

//             Resource resource = new FileSystemResource(realTarget);

//             if (!resource.exists() || !resource.isReadable()) {
//                 throw new NotFoundException("Arquivo não pode ser lido");
//             }

//             long fileSize = Files.size(realTarget);

//             if (fileSize > maxSize) {
//                 throw new BadRequestException(
//                         "Arquivo excede o tamanho máximo permitido para download");
//             }

//             return resource;

//         } catch (SecurityViolationException e) {
//             throw e;

//         } catch (Exception e) {
//             throw new IllegalStateException(
//                     "Erro ao preparar download do arquivo", e);
//         }
//     }
// }
