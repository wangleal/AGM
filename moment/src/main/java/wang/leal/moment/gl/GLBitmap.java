package wang.leal.moment.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLBitmap {

    private int textureId = -1;
    private Bitmap bitmap;
    private float[] mvpMatrix;
    private float[] textureMatrix;
    private boolean isChanged = false;
    private GL2DTextureHelper helper;
    private int x,y,width,height;

    public GLBitmap(Bitmap bitmap){
        helper = new GL2DTextureHelper();
        setBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap){
        releaseBitmap();
        this.bitmap = bitmap;
        isChanged = true;
    }

    public void setPosition(int x,int y){
        this.x = x;
        this.y = y;
    }

    public void create() {
        helper.create();
    }

    public void sizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
        helper.sizeChanged(width,height);
    }

    public void draw() {
        GLES20.glViewport(x,height-y-bitmap.getHeight(),bitmap.getWidth(),bitmap.getHeight());
        helper.draw(getTextureId(),getMVPMatrix(),getTextureMatrix());
        GLES20.glViewport(0,0,width,height);
    }

    private int getTextureId(){
        if (textureId==-1){
            textureId = GLESUtil.create2DTextureId();
        }
        if (isChanged){
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, this.bitmap, 0);
            isChanged = false;
        }
        return textureId;
    }

    private float[] getMVPMatrix() {
        if (mvpMatrix==null){
            mvpMatrix = MatrixUtil.getOriginMatrix();
            MatrixUtil.flipY(mvpMatrix);
        }
        return this.mvpMatrix;
    }

    private float[] getTextureMatrix() {
        if (textureMatrix==null){
            textureMatrix = MatrixUtil.getOriginMatrix();
        }
        return this.textureMatrix;
    }

    public void release() {
        helper.release();
        releaseBitmap();
    }

    private void releaseBitmap(){
        if (this.bitmap!=null&&!this.bitmap.isRecycled()){
            this.bitmap.recycle();
            this.bitmap = null;
        }
    }
}
