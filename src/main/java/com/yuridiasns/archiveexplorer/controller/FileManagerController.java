package com.yuridiasns.archiveexplorer.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.yuridiasns.archiveexplorer.exception.BadRequestException;
import com.yuridiasns.archiveexplorer.model.ApiResponse;
import com.yuridiasns.archiveexplorer.model.fileManager.request.DeleteFileRequest;
import com.yuridiasns.archiveexplorer.model.fileManager.response.DeleteFileResponse;
import com.yuridiasns.archiveexplorer.model.fileManager.response.UploadFileResponse;
import com.yuridiasns.archiveexplorer.service.FileManagerService;

@RestController
@RequestMapping("/api/manage")
public class FileManagerController {

    private final FileManagerService fileManagerService;

    public FileManagerController(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

    // TODO: Posteriormente, garantir a padronização dos requests de FileManager via
    // body (não mais via query param)
    @DeleteMapping("/delete")
    public ApiResponse<DeleteFileResponse> delete(
            @RequestBody(required = false) DeleteFileRequest request,
            @RequestParam(required = false) String path) {

        String finalPath = null;

        if (request != null && request.getPath() != null && !request.getPath().isBlank()) {
            finalPath = request.getPath();
        } else if (path != null && !path.isBlank()) {
            finalPath = path;
        }

        if (finalPath == null) {
            throw new BadRequestException("O parâmetro 'path' é obrigatório (via body ou query param)");
        }

        DeleteFileResponse response = fileManagerService.delete(new DeleteFileRequest(finalPath));

        return ApiResponse.success("Arquivo removido com sucesso", response);
    }

    // TODO: Funciona, mas seria uma boa ideia refinar isso depois
    @PostMapping("/upload")
    public ApiResponse<UploadFileResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", required = false) String path) {

        UploadFileResponse response = fileManagerService.upload(file, path);
        return ApiResponse.success("Arquivo enviado com sucesso", response);
    }

}
