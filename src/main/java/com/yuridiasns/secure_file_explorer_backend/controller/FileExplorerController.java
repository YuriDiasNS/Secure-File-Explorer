package com.yuridiasns.secure_file_explorer_backend.controller;

import com.yuridiasns.secure_file_explorer_backend.model.Response.ApiResponse;
import com.yuridiasns.secure_file_explorer_backend.model.Response.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.model.Response.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.service.FileExplorerService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/explorer")
public class FileExplorerController {

    private final FileExplorerService fileExplorerService;

    public FileExplorerController(FileExplorerService fileExplorerService) {
        this.fileExplorerService = fileExplorerService;
    }

    // =========================
    // LISTAR ROOT
    // =========================
    @GetMapping
    public ApiResponse<ExplorerResponse> listRoot() {
        return ApiResponse.success(
                "Diretório listado com sucesso",
                fileExplorerService.listRoot());
    }

    // =========================
    // INFO
    // =========================
    @GetMapping("/info")
    public ApiResponse<FileInfoResponse> getInfo(@RequestParam String path) {
        return ApiResponse.success(
                "Informações obtidas com sucesso",
                fileExplorerService.info(path));
    }
}
