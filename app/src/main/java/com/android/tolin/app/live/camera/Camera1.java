package com.android.tolin.app.live.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.android.tolin.app.live.utils.CameraUtil;
import com.android.tolin.app.live.view.AbsGLSurfaceView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 4.4以前的api使用的camera
 */
public class Camera1<T extends Camera> implements ICamera<T> {
    private final static String TAG = Camera1.class.getSimpleName();
    private Camera c = null;
    private WeakReference<AbsGLSurfaceView> glSurfaceView;
    private WeakReference<SurfaceTexture> surfaceTexture;
    private String currCameraId = "0";
    private boolean isPreview = false;
    private Context context;


    public Camera1(AbsGLSurfaceView glSurfaceView, SurfaceTexture surfaceTexture, String cameraId) {
        this.glSurfaceView = new WeakReference<>(glSurfaceView);
        this.surfaceTexture = new WeakReference<>(surfaceTexture);
        this.currCameraId = cameraId;
        this.context = glSurfaceView.getContext().getApplicationContext();
        openCamera(currCameraId);
        initCameraOption();
    }

    private void initCameraOption() {
        if (c == null || glSurfaceView.get() == null) return;
        Camera.Parameters parameters = c.getParameters();
        computerPreviewSize(glSurfaceView.get());
        Size size = getCameraPreviewDataSize();
        parameters.setPreviewSize(size.getWidth(), size.getHeight());
        c.setParameters(parameters);
    }

    private Camera openCamera(String cameraId) {
        c = Camera.open(Integer.valueOf(cameraId));
        currCameraId = cameraId;
        return c;
    }

    @Override
    public void startPreview() {
        if (isPreview) {
            return;
        }
        if (c == null) {
            openCamera(getCurrCameraId());
        }
        if (c != null) {
            try {
                c.setPreviewTexture(surfaceTexture.get());
                c.startPreview();
                isPreview = true;
            } catch (IOException e) {
                relese();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopPreview() {
        context=null;
        relese();
    }

    @Override
    public void pause() {
        if (null == c) return;
        c.stopPreview();
    }

    @Override
    public void resume() {
        if (null == c) return;
        c.startPreview();
    }

    private void relese() {
        synchronized (Camera1.this) {
            if (c != null) {
                try {
                    c.stopPreview();
                    c.release();
                    c = null;
                    isPreview = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destory() {
        relese();
    }

    @Override
    public boolean switchTo(String cameraId) {
        if (c != null) {
            relese();
        }
        openCamera(cameraId);
        return true;
    }

    @Override
    public void switchCamera() {
        if (cameraCount() > 1) {
            if ("0".equals(currCameraId)) {
                switchTo("1");
            } else {
                switchTo("0");
            }
        }
    }

    @Override
    public String getCurrCameraId() {
        return currCameraId + "";
    }

    @Override
    public int cameraCount() {
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        return cameraCount;
    }

    @Override
    public boolean isPreview() {
        return isPreview;
    }

    @Override
    public Size getCameraPreviewDataSize() {

        return computerPreviewSize(glSurfaceView.get());
    }

    @Override
    public Size computerPreviewSize(AbsGLSurfaceView surfaceView) {
        Size surfaceSize = new Size(glSurfaceView.get().getWidth(), glSurfaceView.get().getHeight());
        int preWidth = surfaceSize.width;
        int preHeight = surfaceSize.height;
        List<Camera.Size> preSizes = c.getParameters().getSupportedPreviewSizes();
        Camera.Size cSizes[] = {};
        cSizes = preSizes.toArray(cSizes);
        List<Size> sizes = CameraUtil.convertSize(cSizes);
        Size mPreviewSize = CameraUtil.chooseClosePreviewSize(context, sizes, preWidth, preHeight);
        // Log.i("Camera1", "OptimalSize width: " + mPreviewSize.getWidth() + " height: " + mPreviewSize.getHeight());
        return new Size(mPreviewSize);
    }


}
