package wang.leal.moment.camera;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Camera {@link android.hardware.Camera}
 */
public class Camera {
    private android.hardware.Camera camera;
    private CameraInfo frontCameraInfo = new CameraInfo();
    private CameraInfo backCameraInfo = new CameraInfo();
    private CameraInfo currentCameraInfo = frontCameraInfo;
    private android.hardware.Camera.PreviewCallback previewCallback;

    public Camera(){
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                backCameraInfo.cameraId = i;
                backCameraInfo.facing = CameraInfo.CAMERA_FACING_BACK;
                backCameraInfo.orientation = cameraInfo.orientation;
            }else if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontCameraInfo.cameraId = i;
                frontCameraInfo.facing = CameraInfo.CAMERA_FACING_FRONT;
                frontCameraInfo.orientation = cameraInfo.orientation;
            }
        }
    }

    public void openFront(int desiredLong, int desiredShort, int desiredFps) {
        open(frontCameraInfo.cameraId, desiredLong, desiredShort, desiredFps);
    }

    public void openBack(int desiredLong, int desiredShort, int desiredFps) {
        open(backCameraInfo.cameraId, desiredLong, desiredShort, desiredFps);
    }

    private void open(int cameraId,int desireLong,int desireShort,int desireFps) {
        if (camera!=null){
            close();
        }
        camera = android.hardware.Camera.open(cameraId);
        android.hardware.Camera.Parameters parameters= camera.getParameters();
        CameraUtil.setAdvancedCameraParameters(parameters);
        CameraUtil.setPreviewSize(parameters,desireLong,desireShort);
        CameraUtil.setFixedPreviewFps(parameters,desireFps);
        int[] fpsRange = new int[2];
        parameters.getPreviewFpsRange(fpsRange);
        if (cameraId==frontCameraInfo.cameraId){
            frontCameraInfo.fpsRange = fpsRange;
            frontCameraInfo.width = parameters.getPreviewSize().width;
            frontCameraInfo.height = parameters.getPreviewSize().height;
            currentCameraInfo = frontCameraInfo;
        }else if(cameraId==backCameraInfo.cameraId){
            backCameraInfo.fpsRange = fpsRange;
            backCameraInfo.width = parameters.getPreviewSize().width;
            backCameraInfo.height = parameters.getPreviewSize().height;
            currentCameraInfo = backCameraInfo;
        }
        camera.setParameters(parameters);
    }

    public void tackPhoto(){
        if (camera!=null){
            camera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                    android.hardware.Camera.Parameters ps = camera.getParameters();
                    if(ps.getPictureFormat() == PixelFormat.JPEG){
                        //存储拍照获得的图片
                        String path = save(data);
                        //将图片交给Image程序处理
                        Uri uri = Uri.fromFile(new File(path));
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setDataAndType(uri, "image/jpeg");
                    }
                }
            });
        }
    }

    private String save(byte[] data){
        String path = "/sdcard/"+System.currentTimeMillis()+".jpg";
        try {
            //判断是否装有SD卡
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                //判断SD卡上是否有足够的空间
                String storage = Environment.getExternalStorageDirectory().toString();
                StatFs fs = new StatFs(storage);
                long available = fs.getAvailableBlocks()*fs.getBlockSize();
                if(available<data.length){
                    //空间不足直接返回空
                    return null;
                }
                File file = new File(path);
                if(!file.exists())
                    //创建文件
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    /**
     * Must be calling after open.
     * @return CameraInfo
     */
    public CameraInfo getCameraInfo() {
        return currentCameraInfo;
    }

    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;
    public void startPreview(SurfaceTexture surfaceTexture, android.hardware.Camera.PreviewCallback previewCallback) {
        if (camera!=null){
            this.previewCallback = previewCallback;
            try {
                if (previewCallbackBuffer == null) {
                    previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][currentCameraInfo.width* currentCameraInfo.height * 3 / 2];
                }
                camera.setPreviewCallbackWithBuffer((data, camera) -> {
                    if (Camera.this.previewCallback!=null){
                        Camera.this.previewCallback.onPreviewFrame(data,camera);
                    }
                });
                for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++) {
                    camera.addCallbackBuffer(previewCallbackBuffer[i]);
                }
                camera.setPreviewTexture(surfaceTexture);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (this.previewCallback!=null){
            this.previewCallback = null;
        }
        if (camera!=null){
            try {
                camera.setPreviewCallback(null);
                camera.setPreviewTexture(null);
                //尝试设置为空的  竟然没用,我的天
                //null callbackBuffer will be ignored and won't be added to the queue.
                addCallbackBuffer(null);
                camera.stopPreview();
                camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.previewCallbackBuffer != null){
            this.previewCallbackBuffer = null;
        }
    }

    public void addCallbackBuffer(byte[] data) {
        if (camera!=null){
            camera.addCallbackBuffer(data);
        }
    }
}
