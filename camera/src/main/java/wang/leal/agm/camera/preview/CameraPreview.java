package wang.leal.agm.camera.preview;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import wang.leal.agm.camera.CameraDevice;
import wang.leal.agm.camera.CameraService;

public class CameraPreview {
    private OnPreviewListener onPreviewListener;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    public CameraPreview(TextureView textureView){
        this.textureView = textureView;
        this.cameraDevice = CameraService.getCamera(textureView.getContext());
        this.textureView.setSurfaceTextureListener(new GLSurfaceTextureListener());
    }

    public void startPreview(){
        this.cameraDevice.openBack(textureView.getSurfaceTexture(),100,100,30);
    }

    public void setOnPreviewListener(OnPreviewListener onPreviewListener){
        this.onPreviewListener = onPreviewListener;
    }

    private class GLSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (onPreviewListener!=null){
                onPreviewListener.onPrepared();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    public interface OnPreviewListener{
        void onPrepared();
    }

}
