package wang.leal.moment.recorder;

import android.media.MediaFormat;

public class VideoFormat {
    public static final VideoFormat HW360 = new VideoFormat(360,640,1024 * 400,15,2);
    public static final VideoFormat HW480_848 = new VideoFormat(480,848,1024 * 800,15,2);
    public static final VideoFormat HW480_1200 = new VideoFormat(480,848,1024 * 1200,15,2);
    public static final VideoFormat EQ480_480 = new VideoFormat(480,480,1024 * 1200,15,2);
    public static final VideoFormat HW480_1600 = new VideoFormat(480,848,1024 * 1600,15,2);
    public static final VideoFormat HW720 = new VideoFormat(720,1280,1024 * 2000,20,2);

    public int width;
    public int height;
    public int bitrate;
    public int fps;
    public int interval;//I_FRAME_INTERVAL

    public MediaFormat format;

    public VideoFormat(int width,int height,int bitrate,int fps,int interval){
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.fps = fps;
        this.interval = interval;
    }
}
