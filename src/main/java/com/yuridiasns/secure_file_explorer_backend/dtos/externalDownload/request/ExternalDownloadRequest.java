package com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.request;

import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.DownloadType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public class ExternalDownloadRequest {

    @NotNull(message = "O tipo de download é obrigatório")
    private DownloadType type; // direct | torrent | magnet

    @NotEmpty(message = "A lista de links não pode estar vazia")
    private List<@NotEmpty(message = "O link não pode estar vazio") String> links;

    public ExternalDownloadRequest() {
    }

    public ExternalDownloadRequest(DownloadType type, List<String> links) {
        this.type = type;
        this.links = links;
    }

    public DownloadType getType() {
        return type;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setType(DownloadType type) {
        this.type = type;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
}
