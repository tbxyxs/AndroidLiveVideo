package com.android.tolin.app.live.fragment;

import android.annotation.SuppressLint;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.utils.FileUtil;
import com.android.tolin.app.live.utils.GLShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TestGLFragment extends Fragment {
    private GLSurfaceView glSurfaceView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        glSurfaceView = new GLSurfaceView(container.getContext());
        return glSurfaceView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }

    private void initView() {
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            float vertextFloat[] = {
                    -1, 1,
                    -1, -1,
                    1, -1,
                    1, 1

//                    0.5f, 0.5f, 0.0f, // top
//                    -0.5f, -0.5f, 0.0f, // bottom left
//                    0.5f, -0.5f, 0.0f  // bottom right
            };
            FloatBuffer vertextBuffer = ByteBuffer.allocateDirect(vertextFloat.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(vertextFloat);
            float mapFloat[] = {
                    0f, 0f,
                    0f, 1f,
                    1f, 0f,
                    1f, 1f,
            };
            FloatBuffer mapBuffer = ByteBuffer.allocateDirect(mapFloat.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            int fragmentPosition = 0;
            int vertexPosition = 0;
            int programId = 0;
            float color[] = {1.0f, 1.0f, 1.0f, 1.0f}; //白色

            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {

                GLES30.glClearColor(1, 0, 0, 0);

                //获取shader源码
                String vertextShader = FileUtil.readAssetsToString(getContext(), "test_texture_vertex_shader.glsl");
                String fragmentShader = FileUtil.readAssetsToString(getContext(), "test_vertex_fragment_shader.glsl");
                //创建着色器及关联着色器
                int vertexShaderId = GLShaderHelper.createShader(GLES30.GL_VERTEX_SHADER, vertextShader);
                int fragmentShaderId = GLShaderHelper.createShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader);
                //create program and attach link shader
                programId = GLShaderHelper.createProgram(vertexShaderId, fragmentShaderId);
                GLES30.glUseProgram(programId);//设置应用program
                //获取片段着色器中的uniform地址
                fragmentPosition = GLES30.glGetUniformLocation(programId, "uTextureUnit");//u_Color代表片段着色器中的uniform变量名称，可更改。
                //获取顶点着色器中的uniform顶点位置a_Position变量对应的地址
                vertexPosition = GLES30.glGetAttribLocation(programId, "aPosition");
                //关联着色器中的属性与本地顶点float数组对应的值
                vertextBuffer.position(0);//将FloatBuffer缓冲区中对应的数据指针显式的移到开始位置。
                mapBuffer.position(0);
//                GLES30.glEnableVertexAttribArray(vertexPosition);
//                GLES30.glVertexAttribPointer(vertexPosition, 3, GLES30.GL_FLOAT, false, 0, vertextBuffer);
//                GLES30.glVertexAttribPointer(fragmentPosition, 2, GLES30.GL_FLOAT, false, 0, mapBuffer);
//                GLES30.glEnableVertexAttribArray(fragmentPosition);

            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES30.glViewport(0, 0, width, height);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onDrawFrame(GL10 gl) {
             /*   //绘制图形
                GLES30.glEnableVertexAttribArray(vertexPosition);
                GLES30.glVertexAttribPointer(vertexPosition, 3, GLES30.GL_FLOAT, false, 0, vertextBuffer);

                GLES30.glClear(gl.GL_COLOR_BUFFER_BIT);
//                GLES30.glUniform4f(fragmentPosition, 0.5f, 1f, 0f, 1f);
                GLES30.glUniform4fv(fragmentPosition, 1, color, 0);
                GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);
                GLES30.glDisableVertexAttribArray(vertexPosition);


                //纹理贴图
                int textureId = GLShaderHelper.createTextureForBitmap(getContext(), R.mipmap.ic_launcher);
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES20.glUniform1i(fragmentPosition, 0);*/

            }

        });
//        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
