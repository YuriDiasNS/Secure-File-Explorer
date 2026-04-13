package com.yuridiasns.archiveexplorer.model.fileExplorer.node;

public class FileNode {

    private String name;
    private String type;
    private boolean accessible;
    private String error;

    public FileNode(String name) {
        this.name = name;
        this.type = "file";
        this.accessible = true;
    }

    public static FileNode inaccessible(String name, String error) {
        FileNode view = new FileNode(name);
        view.accessible = false;
        view.error = error;
        return view;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isAccessible() { return accessible; }
    public String getError() { return error; }
}

