package wang.leal.moment.gl;

import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

public class GLRenderer {
    private static final String TAG = "GLRenderer";
    private HandlerThread rendererThread;
    private Handler handler;
    private EGLWindow eglWindow;
    public GLRenderer(Surface surface){
        this(null, surface);
    }

    public GLRenderer(EGLContext sharedContext, Surface surface){
        this(sharedContext, (Object) surface);
    }

    public GLRenderer(SurfaceTexture surfaceTexture){
        this(null, surfaceTexture);
    }

    public GLRenderer(EGLContext sharedContext, SurfaceTexture surfaceTexture){
        this(sharedContext, (Object) surfaceTexture);
    }

    public void queueEvent(Runnable runnable){
        handler.post(runnable);
    }

    private GLRenderer(EGLContext sharedContext, Object surface){
        rendererThread = new HandlerThread(TAG);
        rendererThread.start();
        handler = new Handler(rendererThread.getLooper());
        handler.post(() -> {
            eglWindow = new EGLWindow(sharedContext,surface);
            eglWindow.makeCurrent();
        });
    }

    public void render(Runnable runnable){
        handler.post(()->{
            runnable.run();
            if (eglWindow!=null){
                eglWindow.swapBuffer();
            }
        });
    }

    public void release(){
        handler.post(()->{
            if (eglWindow!=null){
                eglWindow.release();
            }
           handler.removeCallbacksAndMessages(null);
           rendererThread.quitSafely();
        });
    }

}
