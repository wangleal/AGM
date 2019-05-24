package wang.leal.agm.graphics;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import wang.leal.agm.multimedia.camera.Camera1;
import wang.leal.agm.multimedia.camera.CameraInfo;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class CameraGLTextureRender implements GLTextureView.Renderer , SurfaceTexture.OnFrameAvailableListener {

    private Camera1 camera;
    private SurfaceTexture surfaceTexture;
    private GLFBOCamera glCamera;
    private GLFBO2DCamera gl2dCamera;
    private GL2DTextureHelper gl2dHelper;
    private GLFBO2DTexture glfbo2DTexture;
    private int textureId = -1;
    private EGLContext eglContext;
    private boolean isCameraSuccess = true;
    private GLTextureView textureView;

    public CameraGLTextureRender(GLTextureView textureView) {
        this.textureView = textureView;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (!isSwitch) {
            textureView.requestRender();
        }
    }

    public void start(){
        this.camera = new Camera1();
        this.textureView.setEGLContextClientVersion(2);
        this.textureView.setPreserveEGLContextOnPause(true);
        this.textureView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.textureView.setRenderer(this);
        this.textureView.setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public EGLContext getEGLContext() {
        return eglContext;
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
        camera.startPreview(surfaceTexture, (byte[] data, Camera camera) -> {
            camera.addCallbackBuffer(data);
            if (isSwitch) {
                isSwitch = false;
                textureView.requestRender();
            }
        });
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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
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

    @Override
    public void onDrawFrame(GL10 gl) {
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

    @Override
    public void onSurfaceDestroyed() {

    }
}