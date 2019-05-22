package wang.leal.agm.graphics.egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

public class EGLWindow {

    private EGLSurface eglSurface;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;

    /**
     * @param surface   Must be Surface or SurfaceTexture
     */
    public EGLWindow(Object surface){
        this(null,surface);
    }

    /**
     * @param eglSharedContext  sharedContext
     * @param surface   Must be Surface or SurfaceTexture
     */
    public EGLWindow(EGLContext eglSharedContext, Object surface){
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)){
            throw new RuntimeException("EGL initialize error "+EGL14.eglGetError());
        }
        int[] configAttr = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttr,0, configs, 0,configs.length, numConfigs,0)) {
            throw new RuntimeException("EGL choose config error "+EGL14.eglGetError());
        }
        int[] contextAttr = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        if (eglSharedContext==null){
            eglSharedContext = EGL14.EGL_NO_CONTEXT;
        }
        eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], eglSharedContext, contextAttr, 0);
        int[] surfaceAttr = {
                EGL14.EGL_NONE
        };
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0], surface, surfaceAttr, 0);

    }

    public boolean makeCurrent(){
        return EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    public boolean swapBuffer(){
        return EGL14.eglSwapBuffers(eglDisplay, eglSurface);
    }

    public void release(){
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(eglDisplay, eglSurface);
            eglSurface = EGL14.EGL_NO_SURFACE;
        }
        if (eglContext != EGL14.EGL_NO_CONTEXT) {
            EGL14.eglDestroyContext(eglDisplay, eglContext);
            eglContext = EGL14.EGL_NO_CONTEXT;
        }
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglTerminate(eglDisplay);
            eglDisplay = EGL14.EGL_NO_DISPLAY;
        }
    }

}
