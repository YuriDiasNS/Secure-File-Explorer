package com.yuridiasns.secure_file_explorer_backend.controller;

import com.yuridiasns.secure_file_explorer_backend.model.ApiResponse;
import com.yuridiasns.secure_file_explorer_backend.model.ExplorerResponse;
import com.yuridiasns.secure_file_explorer_backend.service.FileExplorerService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/explorer")
public class FileExplorerController {

    private final FileExplorerService fileExplorerService;

    public FileExplorerController(FileExplorerService fileExplorerService) {
        this.fileExplorerService = fileExplorerService;
    }

    // =========================
    // LISTAR DIRETÓRIO
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<?>> listDirectory(
            @RequestParam(defaultValue = "") String path
    ) {
        try {
            ExplorerResponse response = fileExplorerService.list(path);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Diretório listado com sucesso",
                            response
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Erro interno ao listar diretório")
            );
        }
    }

    // =========================
    // DOWNLOAD DE ARQUIVO
    // =========================
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(
            @RequestParam String path
    ) {
        try {
            Resource resource = fileExplorerService.loadAsResource(path);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\""
                    )
                    .body(resource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Erro ao processar download do arquivo")
            );
        }
    }

    // =========================
    // INFO DE ARQUIVO/DIRETÓRIO
    // =========================
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getInfo(
            @RequestParam(defaultValue = "") String path
    ) {
        try {
            Object info = fileExplorerService.info(path);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Informações obtidas com sucesso",
                            info
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Erro ao obter informações")
            );
        }
    }
}
