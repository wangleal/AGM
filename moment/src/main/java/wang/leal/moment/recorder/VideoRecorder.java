package wang.leal.moment.recorder;


import android.content.Context;

import java.nio.ByteBuffer;

import wang.leal.moment.TextureRender;

public class VideoRecorder implements AudioEncoder.Callback {

    private MicRecorder micRecorder;
    private MicEncoder micEncoder;
    private OffScreenEncoder cameraEncoder;
    private MP4Muxer mp4Muxer;
    private VideoFormat videoFormat;
    private TextureRender textureRender;
    public VideoRecorder(Context context, TextureRender textureRender){
        this.textureRender = textureRender;
        micRecorder = new MicRecorder();
        micEncoder = new MicEncoder();
        cameraEncoder = new OffScreenEncoder();
        mp4Muxer = new MP4Muxer(context.getApplicationContext());
        this.textureRender.addCallback(cameraEncoder);
        micRecorder.addCallback(micEncoder);
        micEncoder.addCallback(this);
        micEncoder.addCallback(mp4Muxer);
        cameraEncoder.addCallback(mp4Muxer);
        mp4Muxer.setCallback(filePath -> {
            if (VideoRecorder.this.callback!=null){
                VideoRecorder.this.callback.onVideoComplete(filePath);
            }
        });
    }

    public void startRecord(VideoFormat videoFormat, AudioFormat audioFormat){
        if (videoFormat==null||audioFormat==null){
            return;
        }
        this.videoFormat = videoFormat;
        mp4Muxer.start();
        micRecorder.start(audioFormat);
        micEncoder.start(audioFormat);
    }

    public void stopRecord(){
        if (micRecorder!=null){
            micRecorder.stop();
        }
        if (micEncoder!=null){
            micEncoder.stop();
        }
        if (cameraEncoder!=null){
            cameraEncoder.stop();
        }
        if (mp4Muxer!=null){
            mp4Muxer.stop();
        }
        isAudioEncoderStart = false;
    }

    public void release(){
        if (micRecorder!=null){
            micRecorder.removeCallback(null);
            micRecorder.release();
        }
        if (micEncoder!=null){
            micEncoder.removeCallback(null);
            micEncoder.release();
        }
        if (cameraEncoder!=null){
            cameraEncoder.removeCallback(null);
            cameraEncoder.release();
        }
        if (mp4Muxer!=null){
            mp4Muxer.release();
        }
        if (textureRender!=null){
            textureRender.removeCallback(null);
            textureRender.release();
        }
        if (this.callback!=null){
            this.callback = null;
        }
        isAudioEncoderStart = false;
    }

    private Callback callback;

    private boolean isAudioEncoderStart = false;
    @Override
    public void onAudioConfig(ByteBuffer encodeData, BufferInfo bufferInfo) {
        if (!isAudioEncoderStart&&videoFormat!=null){
            cameraEncoder.start(textureRender.getEglContext(),videoFormat);
            isAudioEncoderStart = true;
        }
    }

    @Override
    public void onAudioPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo) {
        if (!isAudioEncoderStart&&videoFormat!=null){
            cameraEncoder.start(textureRender.getEglContext(),videoFormat);
            isAudioEncoderStart = true;
        }
    }

    @Override
    public void onAudioFormatChange(AudioFormat audioFormat) {
        if (!isAudioEncoderStart&&videoFormat!=null){
            cameraEncoder.start(textureRender.getEglContext(),videoFormat);
            isAudioEncoderStart = true;
        }
    }

    public interface Callback{
        void onVideoComplete(String videoPath);
    }
    public void setCallback(Callback callback){
        this.callback = callback;
    }
}
