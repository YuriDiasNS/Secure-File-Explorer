package com.yuridiasns.secure_file_explorer_backend.model;

public class FileView {

    private String name;
    private String type = "file";

    public FileView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
