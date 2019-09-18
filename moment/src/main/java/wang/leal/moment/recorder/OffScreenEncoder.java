package wang.leal.moment.recorder;

import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;

import wang.leal.moment.camera.CameraRender;
import wang.leal.moment.gl.GL2DTextureHelper;
import wang.leal.moment.gl.GLESUtil;
import wang.leal.moment.gl.GLFBO2DTexture;
import wang.leal.moment.gl.grafika.EglCore;
import wang.leal.moment.gl.grafika.WindowSurface;

public class OffScreenEncoder implements CameraRender.Callback {
    private static final String TAG = "OffScreenEncoder";
    private VideoEncoder videoEncoder;
    private HandlerThread handlerThread;
    private Handler handler;
    private EglCore eglCore;
    private WindowSurface windowSurface;
    private GL2DTextureHelper gl2DTextureHelper;
    private VideoFormat videoFormat;
    private GLFBO2DTexture glfbo2DTexture;
    private boolean isStart = false;
    private float[] offTextureMatrix = MatrixUtil.getOriginMatrix();
    private float[] offMVPMatrix = MatrixUtil.getOriginMatrix();
    private int cameraWidth;
    private int cameraHeight;
    public OffScreenEncoder(){
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(()-> videoEncoder = new SyncVideoHardEncoder());
    }

    public void start(final EGLContext eglContext,VideoFormat videoFormat){
        this.videoFormat = videoFormat;
        offMVPMatrix = MatrixUtil.getCenterCropMatrix(offMVPMatrix,cameraWidth,cameraHeight,videoFormat.width,videoFormat.height);
        handler.post(()->{
            videoEncoder.start(videoFormat);
            try {
                eglCore = new EglCore(eglContext, EglCore.FLAG_RECORDABLE);
                windowSurface = new WindowSurface(eglCore, videoEncoder.getInputSurface(), true);
                windowSurface.makeCurrent();
                gl2DTextureHelper = new GL2DTextureHelper();
                gl2DTextureHelper.create();
                isStart = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stop(){
        isStart = false;
        handler.post(()->{
            videoEncoder.stop();
            videoEncoder.release();
        });
    }

    public void release(){
        isStart = false;
        handler.post(()->{
            videoEncoder.removeCallback(null);
            handlerThread.quitSafely();
            handlerThread = null;
            handler.removeCallbacksAndMessages(null);
        });
    }

    public void addCallback(final VideoEncoder.Callback callback){
        handler.post(()->videoEncoder.addCallback(callback));
    }

    public void removeCallback(final VideoEncoder.Callback callback){
        handler.post(()->videoEncoder.removeCallback(callback));
    }

    @Override
    public void onCreate() {
        glfbo2DTexture = new GLFBO2DTexture();
        glfbo2DTexture.create();
    }

    @Override
    public void onSizeChanged(int width, int height) {
        glfbo2DTexture.sizeChanged(width,height);
        this.cameraWidth = width;
        this.cameraHeight = height;
    }

    @Override
    public int onDraw(final int textureId, final long timestamp) {
        if (!isStart){
            return textureId;
        }
        glfbo2DTexture.draw(textureId);
        handler.post(()->{
            try {
                videoEncoder.draw();
                GLESUtil.clearTransparent();

                gl2DTextureHelper.sizeChanged(videoFormat.width,videoFormat.height);
                gl2DTextureHelper.draw(textureId,offMVPMatrix,offTextureMatrix);
                windowSurface.setPresentationTime(timestamp);
                windowSurface.swapBuffers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return glfbo2DTexture.getTextureId();
    }

    @Override
    public void onRelease() {
        glfbo2DTexture.release();
    }
}
