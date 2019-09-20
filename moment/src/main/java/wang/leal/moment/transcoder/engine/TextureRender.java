/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// from: https://android.googlesource.com/platform/cts/+/lollipop-release/tests/tests/media/src/android/media/cts/TextureRender.java
// blob: 4125dcfcfed6ed7fddba5b71d657dec0d433da6a
// modified: removed unused method bodies
// modified: use GL_LINEAR for GL_TEXTURE_MIN_FILTER to improve quality.
package wang.leal.moment.transcoder.engine;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.util.Log;

import wang.leal.moment.gl.GL2DTextureHelper;
import wang.leal.moment.gl.GLESUtil;
import wang.leal.moment.gl.GLFBO3DTo2DTexture;
import wang.leal.moment.gl.GLWatermark;
import wang.leal.moment.gl.MatrixUtil;

/**
 * Code for rendering a texture onto a surface using OpenGL ES 2.0.
 */
class TextureRender {
    private int mTextureID = GLESUtil.createOESTextureId();

    private GLFBO3DTo2DTexture glfbo3DTo2DTexture;
    private GL2DTextureHelper gl2dHelper;
    private GLWatermark watermark;
    private float[] mvpMatrix;
    private float[] textureMatrix;
    public TextureRender() {

        glfbo3DTo2DTexture = new GLFBO3DTo2DTexture();
        gl2dHelper = new GL2DTextureHelper();
        Bitmap bitmap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.RED);
        watermark = new GLWatermark(bitmap);

    }
    public int getTextureId() {
        return mTextureID;
    }
    public void drawFrame(SurfaceTexture st) {
        GLESUtil.clearTransparent();
        st.getTransformMatrix(MatrixUtil.getOriginMatrix());
        glfbo3DTo2DTexture.draw(mTextureID,mvpMatrix,textureMatrix);
        int textureId = watermark.onDraw(null,glfbo3DTo2DTexture.getTextureId(),System.currentTimeMillis());
        Log.e("TextureRender","render");
        if (gl2dHelper != null) {
            gl2dHelper.draw(textureId);
        }
    }
    /**
     * Initializes GL state.  Call this after the EGL surface has been created and made current.
     */
    public void surfaceCreated() {
        this.mvpMatrix = MatrixUtil.getOriginMatrix();
        MatrixUtil.flipY(mvpMatrix);
        this.textureMatrix = MatrixUtil.getOriginMatrix();
        glfbo3DTo2DTexture.create();
        glfbo3DTo2DTexture.sizeChanged(720,1280);
        gl2dHelper.create();
        gl2dHelper.sizeChanged(720,1280);
        watermark.onCreate(null,null);
        watermark.onSizeChanged(null,720,1280);
    }
}
