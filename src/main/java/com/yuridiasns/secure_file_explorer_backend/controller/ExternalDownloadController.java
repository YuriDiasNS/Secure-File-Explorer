package com.yuridiasns.secure_file_explorer_backend.controller;

import com.yuridiasns.secure_file_explorer_backend.dtos.ApiResponse;
import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.request.ExternalDownloadRequest;
import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.response.DownloadStatusResponse;
import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.response.ExternalDownloadResponse;
import com.yuridiasns.secure_file_explorer_backend.service.ExternalDownloadService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/external-download")
public class ExternalDownloadController {

        private final ExternalDownloadService externalDownloadService;

        public ExternalDownloadController(ExternalDownloadService externalDownloadService) {
                this.externalDownloadService = externalDownloadService;
        }

        // =========================
        // START DOWNLOAD
        // =========================
        @PostMapping
        public ResponseEntity<ApiResponse<ExternalDownloadResponse>> startDownload(
                        @RequestBody ExternalDownloadRequest request) {

                ExternalDownloadResponse response = externalDownloadService.startDownload(request);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Download(s) iniciado(s) com sucesso",
                                                response));
        }

        // =========================
        // LIST ALL DOWNLOAD STATUS
        // =========================
        @GetMapping
        public ResponseEntity<ApiResponse<List<DownloadStatusResponse>>> listDownloads() {

                List<DownloadStatusResponse> response = externalDownloadService.listDownloads();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Status dos downloads obtido com sucesso",
                                                response));
        }

        // =========================
        // GET SPECIFIC DOWNLOAD
        // =========================
        @GetMapping("/{jobId}")
        public ResponseEntity<ApiResponse<DownloadStatusResponse>> getDownloadStatus(
                        @PathVariable String jobId) {

                DownloadStatusResponse response = externalDownloadService.getDownloadStatus(jobId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Status do download obtido com sucesso",
                                                response));
        }

        @DeleteMapping("/{jobId}")
        public ResponseEntity<ApiResponse<Void>> cancelDownload(
                        @PathVariable String jobId) {

                externalDownloadService.cancelDownload(jobId);

                return ResponseEntity.ok(
                                ApiResponse.success("Download cancelado com sucesso", null));
        }
}
