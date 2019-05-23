package wang.leal.agm.multimedia.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

/**
 * Camera {@link android.hardware.Camera}
 */
public class Camera1{
    private android.hardware.Camera camera;
    private CameraInfo frontCameraInfo = new CameraInfo();
    private CameraInfo backCameraInfo = new CameraInfo();
    private CameraInfo currentCameraInfo = frontCameraInfo;
    private Camera.PreviewCallback previewCallback;

    public Camera1(){
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

    /**
     * Must be calling after open.
     * @return CameraInfo
     */
    public CameraInfo getCameraInfo() {
        return currentCameraInfo;
    }

    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;
    public void startPreview(SurfaceTexture surfaceTexture, Camera.PreviewCallback previewCallback) {
        if (camera!=null){
            this.previewCallback = previewCallback;
            try {
                if (previewCallbackBuffer == null) {
                    previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][currentCameraInfo.width* currentCameraInfo.height * 3 / 2];
                }
                camera.setPreviewCallbackWithBuffer((data, camera) -> {
                    if (Camera1.this.previewCallback!=null){
                        Camera1.this.previewCallback.onPreviewFrame(data,camera);
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
