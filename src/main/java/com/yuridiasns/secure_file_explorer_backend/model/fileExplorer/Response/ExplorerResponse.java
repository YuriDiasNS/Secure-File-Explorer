package com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.Response;

import com.yuridiasns.secure_file_explorer_backend.model.fileExplorer.View.DirectoryView;

public class ExplorerResponse {

    private DirectoryView root;

    public ExplorerResponse(DirectoryView root) {
        this.root = root;
    }

    public DirectoryView getRoot() {
        return root;
    }
}
