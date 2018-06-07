package com.android.tolin.app.live.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.android.tolin.app.live.presenter.CameraPresenter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 普通相机预览view
 * <p>
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
public class CameraGLSurfaceView extends AbsGLSurfaceView<CameraPresenter> {
    private CameraPresenter cameraPresenter;

    public CameraGLSurfaceView(Context context) {
        super(context);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CameraPresenter getPresenter() {
        return cameraPresenter;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();

    }

    private void init() {
        cameraPresenter = new CameraPresenter(this);
        cameraPresenter.setCallBackRendener(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override
            public void onDrawFrame(GL10 gl) {

            }
        });
    }

    @Override
    public void onPause() {
        if (cameraPresenter != null) {
            cameraPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (cameraPresenter != null) {
            cameraPresenter.onDestroy();
        }
        super.onDetachedFromWindow();
    }
}
