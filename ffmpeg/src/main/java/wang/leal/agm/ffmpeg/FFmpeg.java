package wang.leal.agm.ffmpeg;

public class FFmpeg {

    static {
        System.loadLibrary("ffmpeg");
    }

    public static native int execute(String... commands);

}
