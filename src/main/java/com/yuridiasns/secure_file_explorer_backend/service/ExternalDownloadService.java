package com.yuridiasns.secure_file_explorer_backend.service;

import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.DownloadType;

import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.request.ExternalDownloadRequest;
import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.response.DownloadStatusResponse;
import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.response.ExternalDownloadResponse;
import com.yuridiasns.secure_file_explorer_backend.external.manager.DownloadJob;
import com.yuridiasns.secure_file_explorer_backend.external.manager.DownloadManager;
import com.yuridiasns.secure_file_explorer_backend.external.strategy.ExternalDownloadStrategy;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalDownloadService {

    private final DownloadManager downloadManager;
    private final List<ExternalDownloadStrategy> strategies;

    public ExternalDownloadService(
            DownloadManager downloadManager,
            List<ExternalDownloadStrategy> strategies) {

        this.downloadManager = downloadManager;
        this.strategies = strategies;
    }

    // =========================
    // START DOWNLOAD
    // =========================
    public ExternalDownloadResponse startDownload(ExternalDownloadRequest request) {

        DownloadType type = request.getType();

        ExternalDownloadStrategy strategy = resolveStrategy(type);

        List<String> jobIds = new ArrayList<>();

        for (String link : request.getLinks()) {

            DownloadJob job = downloadManager.createJob(link);

            jobIds.add(job.getJobId());

            strategy.execute(job); // async dentro da strategy
        }

        return new ExternalDownloadResponse(jobIds);
    }

    // =========================
    // LIST ALL
    // =========================
    public List<DownloadStatusResponse> listDownloads() {

        return downloadManager.getAllJobs()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================
    // GET BY ID
    // =========================
    public DownloadStatusResponse getDownloadStatus(String jobId) {

        DownloadJob job = downloadManager.getJob(jobId);

        if (job == null) {
            throw new RuntimeException("Download não encontrado");
        }

        return toResponse(job);
    }

    // =========================
    // INTERNAL METHODS
    // =========================
    private ExternalDownloadStrategy resolveStrategy(DownloadType type) {

        return strategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Tipo de download não suportado: " + type));
    }

    private DownloadStatusResponse toResponse(DownloadJob job) {

        return new DownloadStatusResponse(
                job.getJobId(),
                job.getFileName(),
                job.getStatus(),
                job.getProgress(),
                job.getMessage()
        );
    }
}
