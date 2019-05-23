package wang.leal.agm.graphics;

import android.opengl.Matrix;

public class GLFBOCamera {
    private float[] mvpMatrix;
    private float[] textureMatrix;
    private GLFBO3DTo2DTexture glfbo3DTo2DTexture;

    public GLFBOCamera(){
        glfbo3DTo2DTexture = new GLFBO3DTo2DTexture();
    }

    public void create() {
        glfbo3DTo2DTexture.create();
    }

    public void sizeChanged(int width, int height) {
        glfbo3DTo2DTexture.sizeChanged(width,height);
    }

    public void draw(int textureId) {
        if (mvpMatrix==null||textureMatrix==null){
            return;
        }
        glfbo3DTo2DTexture.draw(textureId,mvpMatrix,textureMatrix);
    }

    public int getTextureId(){
        return glfbo3DTo2DTexture.getTextureId();
    }

    public void release() {
        glfbo3DTo2DTexture.release();
        mvpMatrix = null;
        textureMatrix = null;
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
        }
        this.textureMatrix = MatrixUtil.getOriginMatrix();
    }
}