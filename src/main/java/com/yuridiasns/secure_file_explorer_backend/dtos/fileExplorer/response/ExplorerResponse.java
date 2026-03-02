package com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.response;

import com.yuridiasns.secure_file_explorer_backend.dtos.fileExplorer.node.DirectoryNode;

public class ExplorerResponse {

    private DirectoryNode root;

    public ExplorerResponse(DirectoryNode root) {
        this.root = root;
    }

    public DirectoryNode getRoot() {
        return root;
    }
}
