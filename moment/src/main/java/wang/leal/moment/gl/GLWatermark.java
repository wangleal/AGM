package wang.leal.moment.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLWatermark{
    private GLBitmap glBitmap;
    private GLFBOHelper glfboHelper;
    private GL2DTextureHelper gl2DTextureHelper;
    public GLWatermark(Bitmap bitmap){
        glBitmap = new GLBitmap(bitmap);
    }

    public void onCreate(GL10 gl, EGLConfig config) {
        glBitmap.create();
        gl2DTextureHelper = new GL2DTextureHelper();
        gl2DTextureHelper.create();
        glfboHelper = new GLFBOHelper();
        glfboHelper.create();
    }

    public void onSizeChanged(GL10 gl, int width, int height) {
        glBitmap.sizeChanged(width,height);
        glBitmap.setPosition(0,0);
        gl2DTextureHelper.sizeChanged(width,height);
        glfboHelper.sizeChanged(width,height);
    }

    public int onDraw(GL10 gl, int textureId, long timestamp) {
        if (glfboHelper==null||glfboHelper.getFrameBuffer()==-1){
            return textureId;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glfboHelper.getFrameBuffer());
        gl2DTextureHelper.draw(textureId);
        glBitmap.draw();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return glfboHelper.getFrameBufferTexture();
    }

    public void onPreviewFrame(byte[] data) {}

    public void onRelease() {
        glBitmap.release();
        gl2DTextureHelper.release();
        glfboHelper.release();
    }
}