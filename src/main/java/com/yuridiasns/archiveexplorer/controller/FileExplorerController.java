package com.yuridiasns.archiveexplorer.controller;

import org.springframework.web.bind.annotation.*;

import com.yuridiasns.archiveexplorer.model.ApiResponse;
import com.yuridiasns.archiveexplorer.model.fileExplorer.response.ExplorerResponse;
import com.yuridiasns.archiveexplorer.model.fileExplorer.response.FileInfoResponse;
import com.yuridiasns.archiveexplorer.service.FileExplorerService;

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

        String filename = resource.getFilename();

        String safeFilename = (filename != null) ? filename.replace("\"", "_") : "download";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + safeFilename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(resource);
    }

}
