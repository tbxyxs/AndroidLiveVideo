package com.android.tolin.app.live.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.tolin.app.live.camera.Runnable;
import com.android.tolin.app.live.presenter.AbsPresenter;
import com.android.tolin.app.live.presenter.LivePresenter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 使用前记得动态检测camera权限
 * <pre>
 * if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
 * ActivityCompat.requestPermissions(getActivity(),
 * new String[]{Manifest.permission.CAMERA},
 * TAKE_PHOTO_REQUEST_CODE);
 * Log.v(TAG, "checkCameraPermission");
 * }
 * 注：在activity中要关闭硬件加速 android:hardwareAccelerated="false"
 * </pre>
 */
public class LiveGLSurfaceView extends AbsGLSurfaceView<LivePresenter> {
    private LivePresenter livePresenter;

    public LiveGLSurfaceView(Context context) {
        super(context);
    }


    public LiveGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LivePresenter getPresenter() {
        return livePresenter;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        livePresenter = new LivePresenter(this);
    }

    @Override
    public void onPause() {
        if (livePresenter != null) {
            livePresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (livePresenter != null) {
            livePresenter.onDestroy();
        }
        super.onDetachedFromWindow();
    }
}
