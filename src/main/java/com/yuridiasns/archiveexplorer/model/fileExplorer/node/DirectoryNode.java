package com.yuridiasns.archiveexplorer.model.fileExplorer.node;

import java.util.ArrayList;
import java.util.List;

public class DirectoryNode {

    private String name;
    private String type = "directory";
    private boolean accessible = true;
    private String error;
    private List<Object> children = new ArrayList<>();

    public DirectoryNode(String name) {
        this.name = name;
    }

    public static DirectoryNode inaccessible(String name, String error) {
        DirectoryNode view = new DirectoryNode(name);
        view.accessible = false;
        view.error = error;
        return view;
    }

    // NOVO: factory para symlink
    public static DirectoryNode symlink(String name, String error) {
        DirectoryNode view = new DirectoryNode(name);
        view.type = "symlink";
        view.accessible = false;
        view.error = error;
        return view;
    }

    public void addChild(Object child) {
        children.add(child);
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isAccessible() { return accessible; }
    public String getError() { return error; }
    public List<Object> getChildren() { return children; }
}

