package com.yuridiasns.secure_file_explorer_backend.model;

import java.util.ArrayList;
import java.util.List;

public class DirectoryView {

    private String name;
    private String type = "directory";
    private List<Object> children = new ArrayList<>();

    public DirectoryView(String name) {
        this.name = name;
    }

    public void addChild(Object child) {
        this.children.add(child);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<Object> getChildren() {
        return children;
    }
}
