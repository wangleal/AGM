package wang.leal.agm.camera.gl;

import android.opengl.GLES20;

public class GLFilter {
    private GLFBOHelper glfboHelper;
    private GL2DTextureHelper gl2DTextureHelper;
    private GLFilterHelper glFilterHelper;
    public GLFilter(){
        glFilterHelper = new GLFilterHelper();
    }

    public void create() {
        glFilterHelper.create();
        gl2DTextureHelper = new GL2DTextureHelper();
        gl2DTextureHelper.create();
        glfboHelper = new GLFBOHelper();
        glfboHelper.create();
    }

    public void sizeChanged(int width, int height) {
        glFilterHelper.sizeChanged(width,height);
        gl2DTextureHelper.sizeChanged(width,height);
        glfboHelper.sizeChanged(width,height);
    }

    public int draw(int textureId) {
        if (glfboHelper==null||glfboHelper.getFrameBuffer()==-1){
            return textureId;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glfboHelper.getFrameBuffer());
        gl2DTextureHelper.draw(textureId);
        glFilterHelper.draw(textureId);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return glfboHelper.getFrameBufferTexture();
    }

    public void release() {
        glFilterHelper.release();
        gl2DTextureHelper.release();
        glfboHelper.release();
    }
}
