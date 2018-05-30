package com.android.tolin.app.live.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.tolin.app.live.camera.CameraRenderer;
import com.android.tolin.app.live.camera.Size;
import com.android.tolin.app.live.utils.CameraHelper;
import com.android.tolin.app.live.utils.Gl2Utils;
import com.android.tolin.app.live.view.LiveGLSurfaceView;
import com.android.tolin.app.live.view.PSurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LivePresenter extends AbsPresenter implements ILive, GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private LiveGLSurfaceView mGLSurfaceView;
    private CameraHelper cameraHelper;
    private CameraRenderer cameraRender;
    private int mTextureID;
    private PSurfaceTexture mSurface;
    private String cameraId = "1";
    private boolean using = false;

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
        Log.v("surface1", "onSurfaceCreated");
        mTextureID = Gl2Utils.createTextureID();
        mSurface = new PSurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        cameraRender = new CameraRenderer(mGLSurfaceView.getResources(), mSurface, cameraId);
        cameraHelper = new CameraHelper(mGLSurfaceView, cameraId, mSurface);
        Size preSize = cameraHelper.getCamera().getPreviewSize();
        cameraRender.onSurfaceCreated(gl, config);
        cameraRender.setDataSize(preSize.getWidth(), preSize.getHeight());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
         Log.v("surface1", "onSurfaceChanged");
        cameraRender.onSurfaceChanged(gl, width, height);
        cameraHelper.startPreview();
        using = false;

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        cameraRender.onDrawFrame(gl);
    }

    /**
     * @return true：使用中
     */
    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public void onPause() {
        if (cameraHelper != null) {
            cameraHelper.destroy();
            cameraHelper = null;
        }
        if (null != cameraRender) {
            cameraRender.destroy();
            cameraRender = null;
        }
    }

    public void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.destroy();
            cameraHelper = null;
        }
        if (null != cameraRender) {
            cameraRender.destroy();
            cameraRender = null;
        }
        mSurface = null;
        mGLSurfaceView = null;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraId() {
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
            if ("0".equals(cameraHelper.getCameraId())) {
                setCameraId("1");
            } else {
                setCameraId("0");
            }
            using = true;
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

    private static class ExeRunnable implements Runnable {

        @Override
        public void run() {

        }
    }
}
