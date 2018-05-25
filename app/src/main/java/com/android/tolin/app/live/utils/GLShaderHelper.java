package com.android.tolin.app.live.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.support.annotation.IdRes;
import android.util.Log;


public class GLShaderHelper {
    public static boolean DEBUG = true;
    private static String TAG = GLShaderHelper.class.getSimpleName();

    /**
     * 创建一个适用于bitmap的纹理
     *
     * @param context
     * @param resId   图像资源id
     */
    public static int createTextureForBitmap(Context context, @IdRes int resId) {
        int[] textureId = new int[1];
        GLES30.glGenTextures(1, textureId, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null) {
            GLES30.glDeleteTextures(1, textureId, 0);
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);//0：解除纹理的绑定
        return textureId[0];
    }

    /**
     * 创建一个纹理id.
     *
     * @return
     */
    public static int createTextureId() {
        int[] texture = new int[1];
        GLES30.glGenTextures(1, texture, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        if (texture[0] == 0) {
            throw new RuntimeException("create textureid error!");
        }
        return texture[0];
    }

    public static boolean validateProgram(int programId) {
        GLES30.glValidateProgram(programId);
        final int[] validateStatus = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_VALIDATE_STATUS, validateStatus, 0);
        glError(1, "link program error:" + GLES30.glGetProgramInfoLog(programId));
        return validateStatus[0] != 0;
    }

    /**
     * 创建program程序，并将顶点、片断着色器链接到program中。
     *
     * @param vertexId   顶点着色器。
     * @param fragmentId 片断着色器。
     * @return 0：失败
     */
    public static int createProgram(int vertexId, int fragmentId) {
        int programId = GLES30.glCreateProgram();
        if (programId == 0
                || vertexId == 0
                || fragmentId == 0) {
            throw new RuntimeException("create program error!");
        }
        linkShaderToProgram(programId, vertexId, fragmentId);
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES30.GL_TRUE) {
            glError(1, "link program error:" + GLES30.glGetProgramInfoLog(programId));
            GLES30.glDeleteProgram(programId);
            programId = 0;
        }
        return programId;
    }

    /**
     * 链接顶点着色器与片段着色器到program程序中。
     *
     * @param vertexId   顶点shader id
     * @param fragmentId 片段shader id.
     * @return
     */
    private static void linkShaderToProgram(int programId, int vertexId, int fragmentId) {
        GLES30.glAttachShader(programId, vertexId);
        GLES30.glAttachShader(programId, fragmentId);
        GLES30.glLinkProgram(programId);
    }

    /**
     * 创建一个shader对象。
     *
     * @param shaderType
     * @return 0：创建失败；
     */
    public static int createShader(int shaderType, String shaderSource) {
        int shaderId = GLES30.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("create shader <shaderType=" + shaderType + "> error!");
        }
        bindShaderSource(shaderId, shaderSource);
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {//判断着色器是否与新的shaderSource编译成功。0：失败
            glError(1, "Compile shader error:" + shaderType);
            glError(1, "GLES30 Error:" + GLES30.glGetShaderInfoLog(shaderId)); //获取着色器对应的日志信息。
            GLES30.glDeleteShader(shaderId);
            shaderId = 0;
        }
        return shaderId;
    }

    /**
     * 绑定shader对应的.glsl源码
     *
     * @param shaderId
     * @param shaderSource glsl源码
     */
    private static void bindShaderSource(int shaderId, String shaderSource) {
        GLES30.glShaderSource(shaderId, shaderSource);//绑定shader 源码。
        GLES30.glCompileShader(shaderId); //编译shader
    }

    public static void glError(int code, Object index) {
        if (DEBUG && code != 0) {
            Log.e(TAG, "glError:" + code + "---" + index);
        }
    }

}
