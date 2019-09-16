package wang.leal.moment;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.view.TextureView;

import wang.leal.moment.camera.Camera;
import wang.leal.moment.camera.CameraInfo;
import wang.leal.moment.gl.GL2DTextureHelper;
import wang.leal.moment.gl.GLESUtil;
import wang.leal.moment.gl.GLFBO2DCamera;
import wang.leal.moment.gl.GLFBO2DTexture;
import wang.leal.moment.gl.GLFBOCamera;
import wang.leal.moment.gl.GLRenderer;

public class TextureRender implements SurfaceTexture.OnFrameAvailableListener {

    private GLRenderer glRenderer;
    private Camera camera;
    private android.hardware.Camera hardCamera;
    private SurfaceTexture surfaceTexture;
    private GLFBOCamera glCamera;
    private GLFBO2DCamera gl2dCamera;
    private GL2DTextureHelper gl2dHelper;
    private GLFBO2DTexture glfbo2DTexture;
    private int textureId = -1;
    private EGLContext eglContext;
    private boolean isCameraSuccess = true;
    private TextureView textureView;

    TextureRender(TextureView textureView) {
        this.textureView = textureView;
        this.camera = new Camera();
    }

    void startCamera(){
        GLSurfaceTextureListener glSurfaceTextureListener = new GLSurfaceTextureListener();
        if (textureView!=null){
            textureView.setSurfaceTextureListener(glSurfaceTextureListener);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (!isSwitch) {
            requestRender();
        }
    }

    private void requestRender(){
        if (glRenderer!=null){
            glRenderer.render(this::onDrawFrame);
        }
    }

    private void onSurfaceCreated() {
        if (!isCameraSuccess){
            return;
        }
        GLESUtil.clearTransparent();
        eglContext = EGL14.eglGetCurrentContext();
        textureId = GLESUtil.createOESTextureId();
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);
        this.glCamera = new GLFBOCamera();
        this.gl2dCamera = new GLFBO2DCamera();
        this.gl2dHelper = new GL2DTextureHelper();
        this.glCamera.create();
        this.gl2dCamera.create();
        this.gl2dHelper.create();
        glfbo2DTexture = new GLFBO2DTexture();
        glfbo2DTexture.create();
        openFrontCamera();
    }

    public EGLContext getEglContext(){
        return eglContext;
    }

    private void onSurfaceChanged(int width, int height) {
        if (!isCameraSuccess){
            return;
        }
        if (camera!=null){
            CameraInfo cameraInfo = camera.getCameraInfo();
            if (cameraInfo==null||cameraInfo.width==0||cameraInfo.height==0){
                return;
            }
            width = cameraInfo.height*height/cameraInfo.width;//CameraInfo width是长边，height是短边
        }
        if (glCamera != null) {
            glCamera.sizeChanged(width, height);
        }
        if (gl2dCamera != null) {
            gl2dCamera.sizeChanged(width, height);
        }
        if (gl2dHelper != null) {
            gl2dHelper.sizeChanged(width, height);
        }
        if (glfbo2DTexture != null) {
            glfbo2DTexture.sizeChanged(width, height);
        }
    }

    private void onDrawFrame() {
        if (!isCameraSuccess){
            return;
        }
        GLESUtil.clearTransparent();
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        int texture = textureId;
        if (glCamera != null) {
            glCamera.draw(texture);
            texture = glCamera.getTextureId();
        }
        if (gl2dHelper != null) {
            gl2dHelper.draw(texture);
        }
    }

    private boolean isSwitch = false;

    public void switchCamera() {
        isSwitch = true;
        CameraInfo currentCameraInfo = camera.getCameraInfo();
        if (currentCameraInfo != null) {
            if (currentCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                openBackCamera();
            } else if (currentCameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                openFrontCamera();
            }
        }
    }

    private void openFrontCamera() {
        try {
            camera.openFront(textureView.getHeight(), textureView.getWidth(), 24);
            startPreview();
            CameraInfo currentCameraInfo = camera.getCameraInfo();
            if (currentCameraInfo != null) {
                glCamera.previewFront(currentCameraInfo.orientation);
                gl2dCamera.previewFront(currentCameraInfo.orientation);
            }
            isCameraSuccess = true;
        }catch (Exception e){
            e.printStackTrace();
            isCameraSuccess = false;
        }
    }

    private void openBackCamera() {
        try {
            camera.openBack(textureView.getHeight(), textureView.getWidth(), 24);
            startPreview();
            CameraInfo currentCameraInfo = camera.getCameraInfo();
            if (currentCameraInfo != null) {
                glCamera.previewBack(currentCameraInfo.orientation);
                gl2dCamera.previewBack(currentCameraInfo.orientation);
            }
            isCameraSuccess = true;
        }catch (Exception e){
            e.printStackTrace();
            isCameraSuccess = false;
        }
    }

    private void startPreview() {
        if (surfaceTexture == null) {
            return;
        }
        camera.startPreview(surfaceTexture, (byte[] data, android.hardware.Camera camera) -> {
            this.hardCamera = camera;
            camera.addCallbackBuffer(data);
            if (isSwitch) {
                isSwitch = false;
                requestRender();
            }
        });
    }

    public void handleZoom(boolean isZoomIn) {
        if (hardCamera==null)return;
        android.hardware.Camera.Parameters params = hardCamera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            hardCamera.setParameters(params);
        }
    }

    public synchronized void release() {
        if (camera != null) {
            camera.close();
            camera = null;
        }
        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
        if (gl2dHelper != null) {
            gl2dHelper.release();
            gl2dHelper = null;
        }
        if (glCamera != null) {
            glCamera.release();
            glCamera = null;
        }
        if (gl2dCamera != null) {
            gl2dCamera.release();
            gl2dCamera = null;
        }
    }

    private class GLSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            glRenderer = new GLRenderer(surface);
            glRenderer.queueEvent(()->{
                onSurfaceCreated();
                onSurfaceChanged(width,height);
            });
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (glRenderer==null)return;
            glRenderer.queueEvent(()-> onSurfaceChanged(width,height));
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (glRenderer==null)return true;
            glRenderer.queueEvent(()->{
                if (glRenderer!=null){
                    glRenderer.release();
                }
            });
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

}