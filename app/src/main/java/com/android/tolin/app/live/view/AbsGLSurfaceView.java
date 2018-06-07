package com.android.tolin.app.live.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.android.tolin.app.live.presenter.AbsPresenter;
import com.android.tolin.app.live.presenter.ILive;

public abstract class AbsGLSurfaceView<T extends AbsPresenter> extends GLSurfaceView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AbsGLSurfaceView(Context context) {
        super(context);
    }

    public AbsGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract T getPresenter();
}
