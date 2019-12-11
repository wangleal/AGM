package wang.leal.agm.camera;

import android.graphics.SurfaceTexture;

public interface CameraDevice {

    void openFront(SurfaceTexture surfaceTexture,int desiredLong, int desiredShort, int desiredFps);
    void openBack(SurfaceTexture surfaceTexture,int desiredLong, int desiredShort, int desiredFps);
    void requestFocus();
    void close();

}
