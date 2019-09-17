package wang.leal.moment.recorder;

import android.media.MediaFormat;

public class AudioFormat {
    public static final AudioFormat SINGLE_CHANNEL_22050 = new AudioFormat(1,22050,1024*32,android.media.AudioFormat.CHANNEL_IN_MONO,android.media.AudioFormat.ENCODING_PCM_16BIT);
    public static final AudioFormat SINGLE_CHANNEL_44100 = new AudioFormat(1,44100,1024*64,android.media.AudioFormat.CHANNEL_IN_MONO,android.media.AudioFormat.ENCODING_PCM_16BIT);
    public static final AudioFormat SINGLE_CHANNEL_16000 = new AudioFormat(1,16000,1024*32,android.media.AudioFormat.CHANNEL_IN_MONO,android.media.AudioFormat.ENCODING_PCM_16BIT);
    public int channelCount;
    public int sampleRate;
    public int bitrate;
    public int channelConfig;
    public int bytePerSample;

    public MediaFormat format;
    private AudioFormat(int channelCount,int sampleRate,int bitrate,int channelConfig,int bytePerSample){
        this.channelCount = channelCount;
        this.sampleRate = sampleRate;
        this.bitrate = bitrate;
        this.channelConfig = channelConfig;
        this.bytePerSample = bytePerSample;
    }
}
