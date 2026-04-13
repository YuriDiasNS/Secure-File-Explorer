package com.yuridiasns.archiveexplorer.model.fileExplorer.response;

import com.yuridiasns.archiveexplorer.model.fileExplorer.node.DirectoryNode;

public class ExplorerResponse {

    private DirectoryNode root;

    public ExplorerResponse(DirectoryNode root) {
        this.root = root;
    }

    public DirectoryNode getRoot() {
        return root;
    }
}
