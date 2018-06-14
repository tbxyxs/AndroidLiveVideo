package com.android.tolin.app.live.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.widget.Toast;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.utils.CameraUtil;
import com.android.tolin.app.live.view.AbsGLSurfaceView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 5.0及以上使用的camera为
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2<T extends CameraManager> implements ICamera<T> {
    private Context context;
    private WeakReference<AbsGLSurfaceView> glSurfaceView;
    private Size mPreviewSize;
    private WeakReference<SurfaceTexture> surfaceTexture;
    private HandlerThread mThreadHandler;
    private Handler mPreviewHandler;
    private CameraManager cameraManager;
    private String currCameraId = "0";
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;

    /**
     * 信号量控制器, 防止相机没有关闭时退出本应用(若没有关闭就退出, 会造成其他应用无法调用相机)
     * 当某处获得这个许可时, 其他需要许可才能执行的代码需要等待许可被释放才能获取
     */
    private Semaphore mCameraOpenCloseLock = null;
    private boolean isPreview = false;
    private Surface videoSurface;//录制视频的outsurface

    public Camera2(AbsGLSurfaceView glSurfaceView, SurfaceTexture surfaceTexture, String cameraId) {
        this.currCameraId = cameraId;
        this.context = glSurfaceView.getContext().getApplicationContext();
        this.surfaceTexture = new WeakReference<>(surfaceTexture);
        this.glSurfaceView = new WeakReference<>(glSurfaceView);
        initLooper();
        initCameraOption();
    }

    private void initCameraOption() {
        //获得CameraManager
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        computerPreviewSize(glSurfaceView.get());
    }


    //很多过程都变成了异步的了，所以这里需要一个子线程的looper
    private void initLooper() {
        if (mThreadHandler != null)
            return;
        mThreadHandler = new HandlerThread("CameraPreviewThread");
        mThreadHandler.start();
        mPreviewHandler = new Handler(mThreadHandler.getLooper());
    }


    @SuppressLint("MissingPermission")
    private void openCamera(String cameraId) {
        this.currCameraId = cameraId;
        mCameraOpenCloseLock = new Semaphore(1);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            cameraManager.openCamera(currCameraId, stateCallback, mPreviewHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            //   e.printStackTrace();
            toastNoAccess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            throw new RuntimeException("Camera 没有使用权限！");
            toastNoAccess();
            return;
        }
        if (mCameraDevice != null) {
            startPreview(mCameraDevice);
            return;
        }
        openCamera(currCameraId);
    }

    /**
     * toast 没有权限提示
     */
    private void toastNoAccess() {
        glSurfaceView.get().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.live_string_toast_camera_no_access, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void stopPreview() {
        // 关闭捕获会话
        if (null != mSession) {
            mSession.close();
            mSession = null;
            videoSurface = null;
            isPreview = false;
        }
    }

    private void stopBackgroundThread() {
        if (mThreadHandler == null) {
            return;
        }
        mThreadHandler.quitSafely();
        try {
            mThreadHandler.join();
            mThreadHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        destory();
        stopBackgroundThread();
    }

    @Override
    public void onResume() {

    }


    @Override
    public void destory() {
        stopBackgroundThread();
        // 获得相机开打关闭许可
        try {
            if (mCameraOpenCloseLock != null) {
                mCameraOpenCloseLock.acquire();
            }
            // 关闭捕获会话
            if (null != mSession) {
                mSession.close();
                mSession = null;
            }
            // 关闭当前相机
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            videoSurface = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mCameraOpenCloseLock != null) {
                    mCameraOpenCloseLock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (mThreadHandler != null) {
                mThreadHandler.quitSafely();
                mThreadHandler.join();
                mThreadHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean switchTo(String cameraId) {
        return false;
    }

    @Override
    public void switchCamera() {

    }

    @Override
    public String getCurrCameraId() {
        return currCameraId;
    }

    @Override
    public int cameraCount() {
        try {
            return cameraManager.getCameraIdList().length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
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
        Size size = new Size(preWidth, preHeight);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = cameraManager.getCameraCharacteristics(currCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            android.util.Size[] cSizes = map.getOutputSizes(SurfaceTexture.class);
            List<Size> sizes = CameraUtil.convertSize(cSizes);
//            mPreviewSize = CameraUtil.chooseRatioPreviewSize(context, preWidth, preHeight, sizes);
            mPreviewSize = CameraUtil.chooseClosePreviewSize(context, sizes, preWidth, preHeight);
            //   Log.i("Camera2", "OptimalSize width: " + mPreviewSize.getWidth() + " height: " + mPreviewSize.getHeight());
            return new Size(mPreviewSize);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public void setCameraFps(int fps) {

    }

    @Override
    public Size computerVideoSize(AbsGLSurfaceView surfaceView) {
        CameraCharacteristics characteristics = null;
        try {
            characteristics = cameraManager.getCameraCharacteristics(currCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            android.util.Size[] cSizes = map.getOutputSizes(SurfaceTexture.class);
            List<Size> sizes = CameraUtil.convertSize(cSizes);
            android.util.Size mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            Size surfaceSize = new Size(glSurfaceView.get().getWidth(), glSurfaceView.get().getHeight());
            int width = surfaceSize.width;
            int height = surfaceSize.height;
            android.util.Size mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);
            Size size = new Size(mPreviewSize);
            return size;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Size getCameraVideoSize() {
        return computerVideoSize(glSurfaceView.get());
    }

    @Override
    public T getCamera() {
        return (T) cameraManager;
    }

    private void reStartPreview() {
        if (mCameraDevice == null) return;
        startPreview(mCameraDevice);
    }

    /**
     * 为相机预览创建新的CameraCaptureSession
     *
     * @param cameraDevice
     */
    private void startPreview(CameraDevice cameraDevice) {
        try {
            surfaceTexture.get().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface surface = new Surface(surfaceTexture.get());
            //设置了一个具有输出Surface的CaptureRequest.Builder。
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(surface);
            if (null != videoSurface) {
                surfaces.add(videoSurface);
                mPreviewRequestBuilder.addTarget(videoSurface);
            }
            cameraDevice.createCaptureSession(surfaces, mSessionStateCallback, mPreviewHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CaptureRequest.Builder mPreviewRequestBuilder;

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {


        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            //创建CameraPreviewSession
            startPreview(cameraDevice);
        }


        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    /**
     * 添加用于录制视频的video surface
     *
     * @param surface
     */
    public void addVideoSurface(Surface surface) {
        this.videoSurface = surface;
    }


    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(CameraCaptureSession session) {
            // Log.v("StateCallback", "onConfigured");
            updatePreview(session);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            //  Log.v("StateCallback", "onConfigureFailed");
//            session.close();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void updatePreview(CameraCaptureSession session) {
            // 相机已经关闭
            if (null == mCameraDevice) {
                return;
            }
            isPreview = true;
            mSession = session;
            // 会话准备好后，我们开始显示预览
            try {
                // 自动对焦应
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // 闪光灯
//            setAutoFlash(mPreviewRequestBuilder);
                // 开启相机预览并添加事件
                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                //发送请求
                session.setRepeatingRequest(mPreviewRequest, null, mPreviewHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static android.util.Size chooseVideoSize(android.util.Size[] choices) {
        for (android.util.Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        //    Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static android.util.Size chooseOptimalSize(android.util.Size[] choices, int width, int height, android.util.Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<android.util.Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (android.util.Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            //   Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<android.util.Size> {

        @Override
        public int compare(android.util.Size lhs, android.util.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
