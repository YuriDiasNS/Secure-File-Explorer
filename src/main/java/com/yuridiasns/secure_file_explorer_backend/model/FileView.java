package com.yuridiasns.secure_file_explorer_backend.model;

public class FileView {

    private String name;
    private String type;
    private boolean accessible;
    private String error;

    public FileView(String name) {
        this.name = name;
        this.type = "file";
        this.accessible = true;
    }

    public static FileView inaccessible(String name, String error) {
        FileView view = new FileView(name);
        view.accessible = false;
        view.error = error;
        return view;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isAccessible() { return accessible; }
    public String getError() { return error; }
}

