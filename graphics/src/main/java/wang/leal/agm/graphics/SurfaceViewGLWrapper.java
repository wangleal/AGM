package wang.leal.agm.graphics;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import wang.leal.agm.graphics.gl.GLRenderer;

public class SurfaceViewGLWrapper {

    private GLRenderer glRenderer;
    private Renderer renderer;
    public SurfaceViewGLWrapper(SurfaceView surfaceView){
        surfaceView.getHolder().addCallback(new SurfaceCallback());
    }

    public void setRenderer(Renderer renderer){
        this.renderer = renderer;
    }

    public void requestRender(){
        if (glRenderer!=null){
            glRenderer.render(() -> {
                if (renderer!=null){
                    renderer.onDrawFrame();
                }
            });
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            glRenderer = new GLRenderer(holder.getSurface());
            glRenderer.queueEvent(()->{
                if (renderer!=null){
                    renderer.onSurfaceCreated();
                }
            });
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            glRenderer.queueEvent(()->{
                if (renderer!=null){
                    renderer.onSurfaceChanged(width,height);
                }
            });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            glRenderer.queueEvent(()->{
                if (glRenderer!=null){
                    glRenderer.release();
                }
            });
        }
    }

    public interface Renderer {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }

}
