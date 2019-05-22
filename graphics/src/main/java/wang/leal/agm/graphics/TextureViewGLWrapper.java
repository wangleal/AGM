package wang.leal.agm.graphics;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import wang.leal.agm.graphics.gl.GLRenderer;

/**
 *  目前只支持主动调用requestRender来进行绘制
 */
public class TextureViewGLWrapper{
    private TextureView textureView;
    private GLRenderer glRenderer;
    private Renderer renderer;
    public TextureViewGLWrapper(TextureView textureView){
        this.textureView = textureView;
        this.textureView.setSurfaceTextureListener(new GLSurfaceTextureListener());
    }

    public void setRenderer(Renderer renderer){
        this.renderer = renderer;
    }

    public SurfaceTexture getSurfaceTexture(){
        return textureView.getSurfaceTexture();
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

    private class GLSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            glRenderer = new GLRenderer(surface);
            if (renderer!=null){
                renderer.onSurfaceCreated();
                renderer.onSurfaceChanged(width,height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (renderer!=null){
                renderer.onSurfaceChanged(width,height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (glRenderer!=null){
                glRenderer.release();
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    public interface Renderer {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }
}
