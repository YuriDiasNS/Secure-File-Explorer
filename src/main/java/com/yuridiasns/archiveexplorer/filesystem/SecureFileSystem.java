package com.yuridiasns.archiveexplorer.filesystem;

import java.nio.file.Path;

import org.springframework.core.io.Resource;

import com.yuridiasns.archiveexplorer.model.fileExplorer.node.DirectoryNode;
import com.yuridiasns.archiveexplorer.model.fileExplorer.response.FileInfoResponse;

public interface SecureFileSystem {

    DirectoryNode exploreTree(Path directory, String logicalName);

    FileInfoResponse getInfo(Path root, Path target);

    Resource loadFileForDownload(Path root, Path target, long maxSize);

}
