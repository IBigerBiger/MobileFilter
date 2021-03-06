package com.xinyuanhxy.mobilefilter.OpenGl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.AttributeSet;

import com.xinyuanhxy.mobilefilter.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by xinyuanhxy on 16/10/26.
 */
public class OpenGlActivity extends Activity{

    private GLSurfaceView mGLSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(mGLSurfaceView);
    }

    public class MyGLSurfaceView extends GLSurfaceView{

        public MyGLSurfaceView(Context context) {
            this(context, null);
        }

        public MyGLSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setEGLContextClientVersion(2);
            setRenderer(new MyRender(getResources()));
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }

    public static class MyRender implements GLSurfaceView.Renderer{

        private static final String VERTEX_SHADER = "attribute vec4 vPosition;" +
                "attribute vec2 a_texCoord;" +
                "varying vec2 v_texCoord;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "  v_texCoord = a_texCoord;" +
                "}";
        private static final String FRAGMENT_SHADER = "precision mediump float;" +
                "varying vec2 v_texCoord;" +
                "uniform sampler2D s_texture;" +
                "void main() {" +
                "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                "}";

        private static final float[] VERTEX = {
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
        };

        private final FloatBuffer mVertexBuffer;

        private static final float[] UV_TEX_VERTEX = {   // in clockwise order:
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
        FloatBuffer mUvTexVertexBuffer;

        private int mPositionHandle;
        private int mTexCoordHandle;
        private int mTexSamplerHandle;


        private int mProgram;

        private final Resources mResources;

        public MyRender(Resources resources) {

            this.mResources = resources;

            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX);
            mVertexBuffer.position(0);

            mUvTexVertexBuffer = ByteBuffer.allocateDirect(UV_TEX_VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(UV_TEX_VERTEX);
            mUvTexVertexBuffer.position(0);

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mProgram = GLES20.glCreateProgram();
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            int[] mTexNames = new int[1];
            GLES20.glGenTextures(1, mTexNames, 0);

            Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.p_300px);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexNames[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
            mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除指定的buffer到预设值。可清除以下四类buffer:
            //1）GL_COLOR_BUFFER_BIT
            //2）GL_DEPTH_BUFFER_BIT
            //3）GL_ACCUM_BUFFER_BIT
            //4）GL_STENCIL_BUFFER_BIT
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            //安装一个program object，并把它作为当前rendering state的一部分。
            GLES20.glUseProgram(mProgram);

            //Enable由索引index指定的通用顶点属性数组。
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            //定义一个通用顶点属性数组。当渲染时，它指定了通用顶点属性数组从索引index处开始的位置和数据格式
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    12, mVertexBuffer);

            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                    mUvTexVertexBuffer);

            GLES20.glUniform1i(mTexSamplerHandle, 0);

            //三个成员变量mode,first,count
            //1) mode:指明render原语，如：GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS, 和 GL_POLYGON。
            //2) first: 指明Enable数组中起始索引。
            //3) count: 指明被render的原语个数。
            //可以预先使用单独的数据定义vertex、normal和color，然后通过一个简单的glDrawArrays构造一系列原语。当调用 glDrawArrays时，它使用每个enable的数组中的count个连续的元素，来构造一系列几何原语，从第first个元素开始
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            //Disable由索引index指定的通用顶点属性数组。
            GLES20.glDisableVertexAttribArray(mPositionHandle);
            GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        }

        //加载Shader代码
        static int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }
    }

}
