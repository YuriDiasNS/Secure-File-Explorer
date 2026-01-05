package com.yuridiasns.secure_file_explorer_backend.model.View;

import java.util.ArrayList;
import java.util.List;

public class DirectoryView {

    private String name;
    private String type = "directory";
    private boolean accessible = true;
    private String error;
    private List<Object> children = new ArrayList<>();

    public DirectoryView(String name) {
        this.name = name;
    }

    public static DirectoryView inaccessible(String name, String error) {
        DirectoryView view = new DirectoryView(name);
        view.accessible = false;
        view.error = error;
        return view;
    }

    // 👇 NOVO: factory para symlink
    public static DirectoryView symlink(String name, String error) {
        DirectoryView view = new DirectoryView(name);
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

