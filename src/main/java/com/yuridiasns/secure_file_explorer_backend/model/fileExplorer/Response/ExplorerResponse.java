package com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.Response;

import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.node.DirectoryNode;

public class ExplorerResponse {

    private DirectoryNode root;

    public ExplorerResponse(DirectoryNode root) {
        this.root = root;
    }

    public DirectoryNode getRoot() {
        return root;
    }
}
