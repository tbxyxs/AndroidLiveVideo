package com.android.tolin.app.live.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;

import com.android.tolin.app.live.camera.Size;

import java.util.ArrayList;
import java.util.List;

public class CameraUtil {

    /**
     * 检查设备是否有摄像头。
     *
     * @param context
     * @return true：有
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

//    /**
//     * 获取相机的最佳预览分辨率
//     *
//     * @param sizes
//     * @param width
//     * @param height
//     * @return
//     */
//    private static Size getCameraPreviewSize(Size[] sizes, int width, int height) {
//        List<Size> collectorSizes = new ArrayList<>();
//        for (Size option : sizes) {
//            if (width > height) {
//                if (option.width > width && option.height > height) {
//                    collectorSizes.add(option);
//                }
//            } else {
//                if (option.height > width && option.width > height) {
//                    collectorSizes.add(option);
//                }
//            }
//        }
//        if (collectorSizes.width() > 0) {
//            return Collections.min(collectorSizes, new Comparator<Size>() {
//                @Override
//                public int compare(Size s1, Size s2) {
//                    return Long.signum(s1.width * s1.height - s2.width * s2.height);
//                }
//            });
//        }
//        return sizes[0];
//    }

    /**
     * 通过对比得到与宽高比最接近的尺寸（如果有相同尺寸，优先选择）
     *
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Size getCloselyPreSize(Context context, int surfaceWidth, int surfaceHeight, List<Size> preSizeList) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (ori == mConfiguration.ORIENTATION_PORTRAIT) {  //竖屏
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Size size : preSizeList) {
            if ((size.getWidth() == reqTmpWidth) && (size.getHeight() == reqTmpHeight)) {
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Size retSize = null;
        for (Size size : preSizeList) {
            curRatio = ((float) size.getWidth()) / size.getHeight();
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

    public static List<Size> convertSize(android.util.Size[] oldSizes) {
        if (oldSizes.length <= 0) return null;
        List<Size> sizes = new ArrayList<>();
        for (android.util.Size size : oldSizes) {
            Size temp = new Size(size);
            sizes.add(temp);
        }
        return sizes;
    }

    public static List<Size> convertSize(Camera.Size[] oldSizes) {
        if (oldSizes.length <= 0) return null;
        List<Size> sizes = new ArrayList<>();
        for (Camera.Size size : oldSizes) {
            Size temp = new Size(size);
            sizes.add(temp);
        }
        return sizes;
    }
}
