package wang.leal.agm.graphics;

import android.opengl.GLES20;

public class GLFBO2DTexture {
    private GLFBOHelper glFBOHelper;
    private GL2DTextureHelper gl2DHelper;

    public GLFBO2DTexture(){
        glFBOHelper = new GLFBOHelper();
        gl2DHelper = new GL2DTextureHelper();
    }

    public void create(){
        glFBOHelper.create();
        gl2DHelper.create();
    }

    public void sizeChanged(int width,int height){
        glFBOHelper.sizeChanged(width,height);
        gl2DHelper.sizeChanged(width,height);
    }

    public void draw(int textureId){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFBOHelper.getFrameBuffer());
        gl2DHelper.draw(textureId);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void draw(int textureId,float[] mvpMatrix,float[] textureMatrix){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFBOHelper.getFrameBuffer());
        gl2DHelper.draw(textureId,mvpMatrix,textureMatrix);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getTextureId(){
        return glFBOHelper.getFrameBufferTexture();
    }

    public void release(){
        gl2DHelper.release();
        glFBOHelper.release();
    }
}
