package wang.leal.agm.camera.newer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.util.Collections;

import wang.leal.agm.camera.CameraDevice;

public class Camera implements CameraDevice {
    private CameraManager cameraManager;
    private CameraDeviceStatusCallback deviceStatusCallback;
    private CameraCaptureStatusCallback captureStatusCallback;
    private android.hardware.camera2.CameraDevice cameraDevice;
    private Context context;
    private int desiredLong, desiredShort, desiredFps;
    private SurfaceTexture cameraSurfaceTexture;
    private Surface cameraSurface;

    public Camera(Context context) {
        this.context = context;
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        deviceStatusCallback = new CameraDeviceStatusCallback();
        captureStatusCallback = new CameraCaptureStatusCallback();
    }

    @Override
    public void openFront(SurfaceTexture surfaceTexture, int desiredLong, int desiredShort, int desiredFps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        try {
            if (cameraSurfaceTexture == null || cameraSurfaceTexture != surfaceTexture) {
                cameraSurfaceTexture = surfaceTexture;
                cameraSurface = new Surface(cameraSurfaceTexture);
            }
            cameraManager.openCamera(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT), deviceStatusCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openBack(SurfaceTexture surfaceTexture, int desiredLong, int desiredShort, int desiredFps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.getPackageManager().checkPermission(Manifest.permission.CAMERA,context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        try {
            if (cameraSurfaceTexture == null || cameraSurfaceTexture != surfaceTexture) {
                cameraSurfaceTexture = surfaceTexture;
                cameraSurface = new Surface(cameraSurfaceTexture);
            }
            cameraManager.openCamera(String.valueOf(CameraCharacteristics.LENS_FACING_BACK), deviceStatusCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestFocus() {

    }

    @Override
    public void close() {

    }

    private class CameraDeviceStatusCallback extends android.hardware.camera2.CameraDevice.StateCallback {

        @Override
        public void onOpened(@NonNull android.hardware.camera2.CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull android.hardware.camera2.CameraDevice camera) {

        }

        @Override
        public void onClosed(@NonNull android.hardware.camera2.CameraDevice camera) {
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull android.hardware.camera2.CameraDevice camera, int error) {
            cameraDevice = null;
        }
    }

    private void startPreview() {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(cameraSurface), captureStatusCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private class CameraCaptureStatusCallback extends android.hardware.camera2.CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(cameraSurface);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest
                        .CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                CaptureRequest previewRequest = previewRequestBuilder.build();
                session.setRepeatingRequest(previewRequest, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    }
}
