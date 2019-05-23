package wang.leal.agm.multimedia.camera;

import java.util.List;

public class CameraUtil {

    public static void setPreviewSize(android.hardware.Camera.Parameters params, int desiredLong,int desiredShort) {
        if (params != null) {
            try {
                float desireRatio = desiredLong*1.0f/desiredShort;
                List<android.hardware.Camera.Size> sizes = params.getSupportedPreviewSizes();
                if (sizes != null && sizes.size() > 0) {
                    int index = 0;
                    float minDexRatio = Float.MAX_VALUE;
                    for (int i = 0; i < sizes.size(); i++) {
                        android.hardware.Camera.Size size = sizes.get(i);
                        if (size == null || size.width < 720) {
                            continue;
                        }
                        float ratio = (float) size.width / (float) size.height;//desireSize 数据是横屏数据
                        float dexRatio = Math.abs(ratio - desireRatio);
                        if (minDexRatio > dexRatio) {
                            minDexRatio = dexRatio;
                            index = i;
                            if (minDexRatio == 0) {
                                break;
                            }
                        }
                    }
                    android.hardware.Camera.Size previewSize = sizes.get(index);
                    params.setPreviewSize(previewSize.width,previewSize.height);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setFixedPreviewFps(android.hardware.Camera.Parameters params, int desiredFps) {
        int desiredThousandFps = desiredFps*1000;
        List<int[]> supported = params.getSupportedPreviewFpsRange();
        for (int[] entry : supported) {
            if ((entry[0] == entry[1]) && (entry[0] == desiredThousandFps)) {
                params.setPreviewFpsRange(entry[0], entry[1]);
                return;
            }
        }

        int[] tmp = new int[2];
        params.getPreviewFpsRange(tmp);
        int guess;
        if (tmp[0] == tmp[1]) {
            guess = tmp[0];
        } else {
            guess = tmp[1] / 2;     // shrug
        }
        params.setPreviewFpsRange(guess,guess);
    }

    public static void setAdvancedCameraParameters(android.hardware.Camera.Parameters paramParameters)
    {
        String flashOffParam = android.hardware.Camera.Parameters.FLASH_MODE_OFF;
        if (isSupported(flashOffParam, paramParameters.getSupportedFlashModes()))
        {
            paramParameters.setFlashMode(flashOffParam);
        }
        String whiteAutoParam = android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;
        if (isSupported(whiteAutoParam, paramParameters.getSupportedWhiteBalance()))
        {
            paramParameters.setWhiteBalance(whiteAutoParam);
        }
        String focusVideoParam = android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        if (isSupported(focusVideoParam, paramParameters.getSupportedFocusModes()))
        {
            paramParameters.setFocusMode(focusVideoParam);
        }
        String antiAutoParam = android.hardware.Camera.Parameters.ANTIBANDING_AUTO;
        if (isSupported(antiAutoParam, paramParameters.getSupportedAntibanding()))
        {
            paramParameters.setAntibanding(antiAutoParam);
        }
        String sceneAutoParam = android.hardware.Camera.Parameters.SCENE_MODE_AUTO;
        if (isSupported(sceneAutoParam, paramParameters.getSupportedSceneModes()))
        {
            paramParameters.setSceneMode(sceneAutoParam);
        }
    }

    public static boolean isSupported(String paramString, List<String> paramList)
    {
        return paramList != null && paramList.contains(paramString);
    }

}
