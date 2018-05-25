package com.android.tolin.app.live.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public abstract class AbsGLSurfaceView extends GLSurfaceView {
    public AbsGLSurfaceView(Context context) {
        super(context);
    }

    public AbsGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
