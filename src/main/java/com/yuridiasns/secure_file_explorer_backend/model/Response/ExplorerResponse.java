package com.yuridiasns.secure_file_explorer_backend.model.Response;

import com.yuridiasns.secure_file_explorer_backend.model.View.DirectoryView;

public class ExplorerResponse {

    private DirectoryView root;

    public ExplorerResponse(DirectoryView root) {
        this.root = root;
    }

    public DirectoryView getRoot() {
        return root;
    }
}
