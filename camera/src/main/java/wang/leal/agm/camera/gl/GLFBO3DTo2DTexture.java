package wang.leal.agm.camera.gl;

import android.opengl.GLES20;

public class GLFBO3DTo2DTexture {
    private GLFBOHelper glFBOHelper;
    private GLOESTextureHelper glOESHelper;

    public GLFBO3DTo2DTexture(){
        glFBOHelper = new GLFBOHelper();
        glOESHelper = new GLOESTextureHelper();
    }

    public void create(){
        glFBOHelper.create();
        glOESHelper.create();
    }

    public void sizeChanged(int width,int height){
        glFBOHelper.sizeChanged(width,height);
        glOESHelper.sizeChanged(width,height);
    }

    public void draw(int textureId){
        if (glFBOHelper==null||glFBOHelper.getFrameBuffer()==-1){
            return;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFBOHelper.getFrameBuffer());
        glOESHelper.draw(textureId);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void draw(int textureId,float[] mvpMatrix,float[] textureMatrix){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFBOHelper.getFrameBuffer());
        glOESHelper.draw(textureId,mvpMatrix,textureMatrix);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getTextureId(){
        return glFBOHelper.getFrameBufferTexture();
    }

    public void release(){
        glOESHelper.release();
        glFBOHelper.release();
    }
}
