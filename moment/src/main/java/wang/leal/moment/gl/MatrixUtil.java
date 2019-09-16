package wang.leal.moment.gl;

import android.opengl.Matrix;

public class MatrixUtil {

    public static float[] getOriginMatrix(){
        return new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1,
        };
    }

    public static float[] flipX(float[] matrix){
        Matrix.scaleM(matrix,0,-1,1,1);
        return matrix;
    }

    public static float[] flipY(float[] matrix){
        Matrix.scaleM(matrix,0,1,-1,1);
        return matrix;
    }

    public static float[] getCenterCropMatrix(float[] matrix,int oriWidth,int oriHeight,int targetWidth,
                                              int targetHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float sWHOri=(float)oriWidth/oriHeight;
        float sWHTarget=(float)targetWidth/targetHeight;
        if(sWHOri>sWHTarget){
            Matrix.orthoM(projection,0,-sWHTarget/sWHOri,sWHTarget/sWHOri,-1,1,1,3);
        }else{
            Matrix.orthoM(projection,0,-1,1,-sWHOri/sWHTarget,sWHOri/sWHTarget,1,3);
        }
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    public static float[] getFitXYMatrix(float[] matrix,int oriWidth,int oriHeight,int targetWidth,
                                              int targetHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        Matrix.orthoM(projection,0,-1,1,-1,1,1,3);
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);

        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    public static float[] getCenterInsideMatrix(float[] matrix,int oriWidth,int oriHeight,int targetWidth,
                                              int targetHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float sWHOri=(float)oriWidth/oriHeight;
        float sWHTarget=(float)targetWidth/targetHeight;
        if(sWHOri>sWHTarget){
            Matrix.orthoM(projection,0,-1,1,-sWHOri/sWHTarget,sWHOri/sWHTarget,1,3);
        }else{
            Matrix.orthoM(projection,0,-sWHTarget/sWHOri,sWHTarget/sWHOri,-1,1,1,3);
        }
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    public static float[] getFitStartMatrix(float[] matrix,int oriWidth,int oriHeight,int targetWidth,
                                              int targetHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float sWHOri=(float)oriWidth/oriHeight;
        float sWHTarget=(float)targetWidth/targetHeight;
        if(sWHOri>sWHTarget){
            Matrix.orthoM(projection,0,-1,1,1-2*sWHOri/sWHTarget,1,1,3);
        }else{
            Matrix.orthoM(projection,0,-1,2*sWHTarget/sWHOri-1,-1,1,1,3);
        }
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    public static float[] getFitEndMatrix(float[] matrix,int oriWidth,int oriHeight,int targetWidth,
                                              int targetHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float sWHOri=(float)oriWidth/oriHeight;
        float sWHTarget=(float)targetWidth/targetHeight;
        if(sWHOri>sWHTarget){
            Matrix.orthoM(projection,0,-1,1,-1,2*sWHOri/sWHTarget-1,1,3);
        }else{
            Matrix.orthoM(projection,0,1-2*sWHTarget/sWHOri,1,-1,1,1,3);
        }
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }
}
