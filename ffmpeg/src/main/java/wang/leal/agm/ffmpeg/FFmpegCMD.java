package wang.leal.agm.ffmpeg;

class FFmpegCMD {

    static {
        System.loadLibrary("ffmpeg");
    }

    private static OnProgressListener onProgressListener;
    public static void setOnProgressListener(OnProgressListener onProgressListener){
        FFmpegCMD.onProgressListener = onProgressListener;
    }

    public static native int execute(String... commands);

    //C 回调
    public static void onProgress(float progress) {
        if (onProgressListener!=null){
            onProgressListener.onProgress(progress);
        }
    }

    public interface OnProgressListener{
        void onProgress(float progress);
    }

}
