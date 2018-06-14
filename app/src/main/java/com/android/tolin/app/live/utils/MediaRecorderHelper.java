package com.android.tolin.app.live.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import com.android.tolin.app.live.camera.Camera2;
import com.android.tolin.app.live.camera.ICamera;
import com.android.tolin.app.live.camera.Size;
import com.android.tolin.app.live.view.AbsGLSurfaceView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * 录制视频helper
 */
public class MediaRecorderHelper {
    private static final String TAG = MediaRecorderHelper.class.getSimpleName();
    private CameraHelper cameraHelper;
    private WeakReference<AbsGLSurfaceView> glSurfaceView;
    private final Context context;
    private MediaRecorder mMediaRecorder;
    private File file = null;
    private boolean mIsRecordingVideo = false;//是否正在录制

    public MediaRecorderHelper(CameraHelper cameraHelper, AbsGLSurfaceView surfaceView) {
        this.glSurfaceView = new WeakReference<>(surfaceView);
        this.context = glSurfaceView.get().getContext().getApplicationContext();
        this.cameraHelper = cameraHelper;
    }

    private void initOption() throws IOException {
        this.mMediaRecorder = new MediaRecorder();
        // 系统版本<5.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频来源
            mMediaRecorder.setCamera((Camera) cameraHelper.getCamera());
        } else {
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);//视频来源
        }
//        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
//        switch (mSensorOrientation) {
//            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
//                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
//                break;
//            case SENSOR_ORIENTATION_INVERSE_DEGREES:
//                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
//                break;
//        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOutputFile(file.getPath());
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);   // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        Size size = cameraHelper.getCamera().getCameraVideoSize();
        mMediaRecorder.setVideoSize(size.getWidth(), size.getHeight());  // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.i(TAG, "onError-->what=" + what + " extra=" + extra);
            }
        });
        // 准备录制
        mMediaRecorder.prepare();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        } else {
//            mMediaRecorder.setPrevfiewDisplay(glSurfaceView.get().getHolder().getSurface());
//            mMediaRecorder.setPreviewDisplay(mMediaRecorder.getSurface());
            Surface recorderSurface = mMediaRecorder.getSurface();
            Object obj = cameraHelper.getCamera();
            if (obj instanceof Camera2) {
                Camera2 camera2 = (Camera2) obj;
                camera2.addVideoSurface(recorderSurface);
            }
        }

    }

    public void start(File file) throws IOException {
        this.file = new File(file.getPath());
        initOption();
        cameraHelper.startPreview();
        // 开始录制
        mMediaRecorder.start();
        mIsRecordingVideo = true;
    }

    @SuppressLint("NewApi")
    public void stop() {
        if (mMediaRecorder != null
                && isRecordingVideo()) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mIsRecordingVideo = false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause() {
        if (!isRecordingVideo()
                || mMediaRecorder == null)
            return;
        mMediaRecorder.pause();
        mIsRecordingVideo = false;
    }

    /**
     * @return true：录制中
     */
    public boolean isRecordingVideo() {
        return mIsRecordingVideo;
    }

    public void destroy() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {

            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mIsRecordingVideo = false;
        }
        cameraHelper = null;
    }

    public void onPause() {
        stop();
    }

    public void onResume() {
        //暂时不支持断点续录功能
    }
}
