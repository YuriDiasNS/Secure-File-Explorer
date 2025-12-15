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

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }
}