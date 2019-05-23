package wang.leal.agm.graphics;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLOESTextureHelper {
    private int textureSamplerHandle;
    private int mvpMatrixHandle;
    private int textureMatrixHandle;
    private int positionHandle;
    private int textureCoordinateHandle;
    private int program;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int COORDS_PER_VERTEX = 2;// number of coordinates per vertex in this array
    private int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float[] originMvpMatrix = MatrixUtil.getOriginMatrix(),originTextureMatrix = MatrixUtil.getOriginMatrix();
    private static final String vertexShaderSource =
            "attribute vec4 aPosition;\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTextureMatrix;\n" +
                    "attribute vec4 aTextureCoordinate;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main()\n" +
                    "{\n" +
                    "  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "}";
    private static final String fragmentShaderSource =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "uniform samplerExternalOES uTextureSampler;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main()\n" +
                    "{\n" +
                    "  gl_FragColor = texture2D( uTextureSampler, vTextureCoord );\n" +
                    "}\n";

    public void create(){
        float[] vertexData = {
                -1.0f,1.0f,
                1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f,
        };
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexData, 0, vertexData.length).position(0);
        float[] textureData = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData, 0, textureData.length).position(0);

        program = GLESUtil.createProgram(vertexShaderSource,fragmentShaderSource);
        textureSamplerHandle= GLES20.glGetUniformLocation(program, "uTextureSampler");
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        textureMatrixHandle = GLES20.glGetUniformLocation(program, "uTextureMatrix");
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        textureCoordinateHandle = GLES20.glGetAttribLocation(program, "aTextureCoordinate");
    }

    public void sizeChanged(int width, int height){
        GLES20.glViewport(0, 0, width, height);
    }

    public void draw(int textureId){
        this.draw(textureId,originMvpMatrix,originTextureMatrix);
    }

    public void draw(int textureId, float[] mvpMatrix, float[] textureMatrix){
        GLES20.glUseProgram(program);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(textureSamplerHandle, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(textureMatrixHandle, 1, false, textureMatrix, 0);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0,4);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

    public void release(){
        vertexBuffer = null;
        textureBuffer = null;
    }
}
