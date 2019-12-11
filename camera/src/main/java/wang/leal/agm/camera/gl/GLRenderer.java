package wang.leal.agm.camera.gl;

import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;

public class GLRenderer {
    private static final String TAG = "GLRenderer";
    private HandlerThread rendererThread;
    private Handler handler;
    private EGLWindow eglWindow;

    public void queueEvent(Runnable runnable){
        handler.post(runnable);
    }

    public GLRenderer(){
        rendererThread = new HandlerThread(TAG+Math.random());
        rendererThread.start();
        handler = new Handler(rendererThread.getLooper());
    }

    public void initWindow(Object surface){
        this.initWindow(null,surface);
    }

    public void initWindow(EGLContext sharedContext,Object surface){
        handler.removeCallbacksAndMessages(null);
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
