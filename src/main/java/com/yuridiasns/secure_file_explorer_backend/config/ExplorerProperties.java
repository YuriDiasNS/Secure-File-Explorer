package com.yuridiasns.secure_file_explorer_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "explorer")
public class ExplorerProperties {

    /**
     * Diretório raiz seguro do explorador
     */
    private Path rootPath;

    /**
     * Tamanho máximo permitido para download (em bytes)
     */
    private long maxDownloadSize;

    public long getMaxDownloadSize() {
        return maxDownloadSize;
    }

    public void setMaxDownloadSize(long maxDownloadSize) {
        this.maxDownloadSize = maxDownloadSize;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }
}