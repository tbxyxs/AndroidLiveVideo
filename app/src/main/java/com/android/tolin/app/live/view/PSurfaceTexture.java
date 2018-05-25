package com.android.tolin.app.live.view;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;

public class PSurfaceTexture extends SurfaceTexture {
    private int mTextureID = 0;

    public PSurfaceTexture(int texName) {
        super(texName);
        this.mTextureID = texName;
    }

    @SuppressLint("NewApi")
    public PSurfaceTexture(int texName, boolean singleBufferMode) {
        super(texName, singleBufferMode);
        this.mTextureID = texName;
    }

    @SuppressLint("NewApi")
    private PSurfaceTexture(boolean singleBufferMode) {
        super(singleBufferMode);
    }

    public int getTextureId() {
        return mTextureID;
    }
}
