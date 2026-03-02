package com.yuridiasns.secure_file_explorer_backend.external.strategy;

import com.yuridiasns.secure_file_explorer_backend.dtos.externalDownload.DownloadType;
import com.yuridiasns.secure_file_explorer_backend.external.manager.DownloadJob;

public interface ExternalDownloadStrategy {

    boolean supports(DownloadType type);

    void execute(DownloadJob job);
}