package com.android.tolin.app.live.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LiveGLSurfaceView extends AbsGLSurfaceView {
    public LiveGLSurfaceView(Context context) {
        super(context);
    }

    public LiveGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
