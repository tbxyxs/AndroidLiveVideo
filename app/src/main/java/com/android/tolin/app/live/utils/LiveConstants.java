package com.android.tolin.app.live.utils;

public enum LiveConstants {
    CAMERAPREVIEW("camerapreview", 1), LIVEPREVIEW("livepreview", 2);
    private String name;
    private int index;

    LiveConstants(String priview, int i) {
        this.name = priview;
        this.index = i;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
