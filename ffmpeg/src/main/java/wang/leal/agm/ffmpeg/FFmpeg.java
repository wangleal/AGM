package wang.leal.agm.ffmpeg;

public class FFmpeg {

    static {
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("swresample");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("avfilter");
        System.loadLibrary("ffmpeg");
    }

    public static native int execute(String... commands);

}
