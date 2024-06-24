package com.pmf.awp.project.model;

public enum Access {
    OWNER(3),
    EDITOR(2),
    VIEWER(1);

    private final int level;

    private Access(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
