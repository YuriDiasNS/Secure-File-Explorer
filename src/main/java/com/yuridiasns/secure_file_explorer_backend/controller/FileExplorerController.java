package com.yuridiasns.secure_file_explorer_backend.controller;

import com.yuridiasns.secure_file_explorer_backend.model.Response.ApiResponse;
import com.yuridiasns.secure_file_explorer_backend.model.Response.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.model.Response.FileInfoResponse;
import com.yuridiasns.secure_file_explorer_backend.service.FileExplorerService;

import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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

    // =========================
    // DOWNLOAD
    // =========================
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String path) {

        Resource resource = fileExplorerService.loadAsResource(path);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
