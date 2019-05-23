package wang.leal.agm.graphics;

import android.opengl.GLES20;

public class GLFBOHelper {
    private int[] frameBuffers;
    private int[] frameBufferTextures;

    public void create(){}

    public void sizeChanged(int width, int height){
        initBuffer(width,height);
    }

    public void release(){
        frameBuffers = null;
        frameBufferTextures = null;
    }

    private void initBuffer(int width, int height){
        if (frameBuffers == null) {
            frameBuffers = new int[1];
            int[] renderBuffers = new int[1];
            frameBufferTextures = new int[1];
            GLES20.glGenRenderbuffers(1, renderBuffers, 0);
            GLES20.glGenFramebuffers(1, frameBuffers, 0);
            GLES20.glGenTextures(1, frameBufferTextures, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffers[0]);
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                    height);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D,frameBufferTextures[0], 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, renderBuffers[0]);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        }
    }

    public int getFrameBuffer(){
        if (frameBuffers==null){
            return -1;
        }
        return frameBuffers[0];
    }

    public int getFrameBufferTexture(){
        if (frameBufferTextures == null){
            return -1;
        }
        return frameBufferTextures[0];
    }
}
