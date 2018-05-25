package com.android.tolin.app.live.presenter;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.android.tolin.app.live.camera.CameraRenderer;
import com.android.tolin.app.live.utils.CameraHelper;
import com.android.tolin.app.live.utils.Gl2Utils;
import com.android.tolin.app.live.view.LiveGLSurfaceView;
import com.android.tolin.app.live.view.PSurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LivePresenter extends AbsPresenter implements ILive, GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private final LiveGLSurfaceView mGLSurfaceView;
    private CameraHelper cameraHelper;
    private CameraRenderer cameraRender;
    private int mTextureID;
    private PSurfaceTexture mSurface;
    private int cameraId = 1;

    public LivePresenter(LiveGLSurfaceView glSurfaceView) {
        this.mGLSurfaceView = glSurfaceView;
        initGL();
    }

    private void initGL() {
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureID = Gl2Utils.createTextureID();
        mSurface = new PSurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        cameraRender = new CameraRenderer(mGLSurfaceView.getResources(), mSurface, cameraId);
        cameraHelper = new CameraHelper(mGLSurfaceView.getContext(), cameraId, mSurface);

        Point point = cameraHelper.getCamera().getCameraConfig().getPreSize();
        cameraRender.onSurfaceCreated(gl, config);
        cameraRender.setDataSize(point.x, point.y);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraRender.onSurfaceChanged(gl, width, height);
        cameraHelper.startPreview();

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        cameraRender.onDrawFrame(gl);
    }

    public void onPause() {
        if (cameraHelper != null) {
            cameraHelper.onPause();
        }
    }

    public void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.destroy();
        }
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getCameraId() {
        cameraId = cameraHelper.getCameraId();
        return cameraId;
    }

    @Override
    public void takePic() {

    }

    @Override
    public void recVideo() {

    }

    @Override
    public void filter() {

    }

    @Override
    public void switchCamera() {
        if (cameraHelper.getCameraCount() > 1) {
            if (cameraHelper.getCameraId() == 0) {
                setCameraId(1);
            } else {
                setCameraId(0);
            }
            cameraHelper.destroy();
            cameraRender.destroy();
            mGLSurfaceView.onPause();
            mGLSurfaceView.onResume();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mGLSurfaceView != null) {
            mGLSurfaceView.requestRender();
        }
    }
}
