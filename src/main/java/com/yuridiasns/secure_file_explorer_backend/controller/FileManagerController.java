package com.yuridiasns.secure_file_explorer_backend.controller;

import com.yuridiasns.secure_file_explorer_backend.model.fileManager.Request.DeleteFileRequest;
import com.yuridiasns.secure_file_explorer_backend.model.fileManager.Response.DeleteFileResponse;
import com.yuridiasns.secure_file_explorer_backend.exception.BadRequestException;
import com.yuridiasns.secure_file_explorer_backend.model.ApiResponse;
import com.yuridiasns.secure_file_explorer_backend.service.FileManagerService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage")
public class FileManagerController {

    private final FileManagerService fileManagerService;

    public FileManagerController(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

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

}
