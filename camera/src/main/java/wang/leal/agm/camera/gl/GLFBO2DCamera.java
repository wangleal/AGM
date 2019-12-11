package wang.leal.agm.camera.gl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLFBO2DCamera {
    private float[] mvpMatrix;
    private float[] textureMatrix;
    private GLFBOHelper glFBOHelper;
    private GL2DTextureHelper gl2DHelper;

    public GLFBO2DCamera(){
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
        if (glFBOHelper==null||glFBOHelper.getFrameBuffer()==-1){
            return;
        }
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

    public void previewFront(int orientation){
        this.mvpMatrix = MatrixUtil.getOriginMatrix();
        if (orientation==90){
            Matrix.rotateM(mvpMatrix,0,270,0,0,1);
        }else if(orientation==270){
            Matrix.rotateM(mvpMatrix,0,90,0,0,1);
        }else if(orientation==0){
            Matrix.rotateM(mvpMatrix,0,270,0,0,1);
            MatrixUtil.flipY(mvpMatrix);
        }
        this.textureMatrix = MatrixUtil.getOriginMatrix();
    }

    public void previewBack(int orientation){
        this.mvpMatrix = MatrixUtil.getOriginMatrix();
        if (orientation==90){
            Matrix.rotateM(mvpMatrix,0,270,0,0,1);
            MatrixUtil.flipY(mvpMatrix);
        }else if(orientation==0){
            Matrix.rotateM(mvpMatrix,0,270,0,0,1);
            MatrixUtil.flipY(mvpMatrix);
        }
        this.textureMatrix = MatrixUtil.getOriginMatrix();
    }
}
