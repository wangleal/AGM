package wang.leal.agm.camera;

import android.content.Context;

import wang.leal.agm.camera.older.Camera;

public class CameraService {

    public static CameraDevice getCamera(Context context){
        return getCamera(context,Type.CAMERA_2);
    }

    public static CameraDevice getCamera(Context context,Type type){
        if (type == Type.CAMERA_1) {
            return new Camera();
        }
        return new wang.leal.agm.camera.newer.Camera(context);
    }

    public enum Type{
        CAMERA_1,CAMERA_2
    }
}
