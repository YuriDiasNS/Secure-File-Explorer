package com.yuridiasns.secure_file_explorer_backend.model;

public class ExplorerResponse {

    private DirectoryView root;

    public ExplorerResponse(DirectoryView root) {
        this.root = root;
    }

    public DirectoryView getRoot() {
        return root;
    }
}
