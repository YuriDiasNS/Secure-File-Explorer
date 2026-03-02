package com.yuridiasns.secure_file_explorer_backend.external.strategy;

import com.yuridiasns.secure_file_explorer_backend.config.ExternalDownloadProperties;
import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.DownloadType;
import com.yuridiasns.secure_file_explorer_backend.external.manager.DownloadJob;
import com.yuridiasns.secure_file_explorer_backend.external.manager.DownloadStatus;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.Duration;

@Component
public class DirectDownloadStrategy implements ExternalDownloadStrategy {

    private static final String DOWNLOAD_DIR = "workdir/";

    private final HttpClient httpClient;

    public DirectDownloadStrategy(ExternalDownloadProperties properties) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        this.properties = properties;
    }

    private final ExternalDownloadProperties properties;

    @Override
    public boolean supports(DownloadType type) {
        return type == DownloadType.DIRECT;
    }

    @Override
    @Async("downloadExecutor")
    public void execute(DownloadJob job) {

        job.setStatus(DownloadStatus.IN_PROGRESS);

        Path outputPath = null;

        try {

            URI uri = URI.create(job.getSourceUrl());

            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) &&
                    !"https".equalsIgnoreCase(scheme)) {
                throw new RuntimeException("Protocolo não permitido");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()))
                    .GET()
                    .header("User-Agent", "SecureFileExplorer")
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro HTTP: " + response.statusCode());
            }

            String fileName = extractFileName(uri);
            job.setFileName(fileName);

            Files.createDirectories(Paths.get(DOWNLOAD_DIR));

            String uniqueName = job.getJobId() + "_" + fileName;
            outputPath = Paths.get(DOWNLOAD_DIR, uniqueName);

            long contentLength = response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(-1);

            if (contentLength > 0 &&
                    contentLength > properties.getMaxSizeBytes()) {
                throw new RuntimeException("Arquivo excede o tamanho máximo permitido");
            }

            try (InputStream inputStream = response.body();
                    OutputStream outputStream = Files.newOutputStream(outputPath)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {

                    if (job.isCancelled()) {
                        throw new RuntimeException("Download cancelado pelo usuário");
                    }

                    outputStream.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;

                    if (totalRead > properties.getMaxSizeBytes()) {
                        throw new RuntimeException("Arquivo excede o tamanho máximo permitido");
                    }

                    if (contentLength > 0) {
                        int progress = (int) ((totalRead * 100) / contentLength);
                        job.setProgress(progress);
                    }
                }
            }

            job.setProgress(100);
            job.setStatus(DownloadStatus.COMPLETED);

        } catch (Exception e) {

            // 👇 REMOVE ARQUIVO PARCIAL
            if (outputPath != null) {
                try {
                    Files.deleteIfExists(outputPath);
                } catch (IOException ignored) {
                }
            }

            job.setStatus(DownloadStatus.FAILED);
            job.setMessage(e.getMessage());
        }
    }

    private String extractFileName(URI uri) {

        String path = uri.getPath();

        if (path == null || path.isBlank()) {
            return "download_" + System.currentTimeMillis();
        }

        String name = path.substring(path.lastIndexOf("/") + 1);

        name = name.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        if (name.isBlank()) {
            return "download_" + System.currentTimeMillis();
        }

        return name;
    }
}
