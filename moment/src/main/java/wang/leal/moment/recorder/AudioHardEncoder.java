package wang.leal.moment.recorder;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AudioHardEncoder extends AudioEncoder{
    private static final String TAG = "AudioHardEncoder";
    private MediaCodec audioEncoder = null;
    private MediaFormat mediaFormat;
    private AudioFormat audioFormat;
    private ByteBuffer audioBuffer;

    @Override
    public void start(AudioFormat audioFormat) {
        if (audioEncoder!=null){
            release();
        }
        this.audioFormat = audioFormat;
        mediaFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", audioFormat.sampleRate, audioFormat.channelCount);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, audioFormat.bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        try{
            audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
            audioEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            audioEncoder.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] audioData,int size) {
        if (audioEncoder!=null) {
            int inputBufferId = audioEncoder.dequeueInputBuffer(0);
            if (inputBufferId >= 0) {
                ByteBuffer inputBuffer;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    inputBuffer = audioEncoder.getInputBuffer(inputBufferId);
                } else {
                    ByteBuffer[] buffers = audioEncoder.getInputBuffers();
                    inputBuffer = buffers[inputBufferId];
                }
                if (inputBuffer != null) {
                    inputBuffer.put(audioData, 0, size);
                    // fill inputBuffer with valid data
                    long mPresentTimeUs = System.nanoTime() / 1000;
                    audioEncoder.queueInputBuffer(inputBufferId, 0, size, mPresentTimeUs, 0);
                }
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferId = audioEncoder.dequeueOutputBuffer(bufferInfo, 0);
            if (outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "INFO_TRY_AGAIN_LATER");
            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                audioFormat.format = audioEncoder.getOutputFormat();
                callbackFormatChange(audioFormat);
            } else if (outputBufferId < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        outputBufferId);
            } else{
                ByteBuffer encodedData;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    encodedData = audioEncoder.getOutputBuffer(outputBufferId);
                } else {
                    ByteBuffer[] buffers = audioEncoder.getOutputBuffers();
                    encodedData = buffers[outputBufferId];
                }
                bufferInfo.presentationTimeUs = System.nanoTime()/1000;
                if (encodedData != null) {
                    byte[] bytes = new byte[bufferInfo.size];
                    encodedData.get(bytes,0,bufferInfo.size);
                    audioBuffer = ByteBuffer.allocateDirect(bufferInfo.size);
                    audioBuffer.put(bytes,0,bufferInfo.size);
                    audioBuffer.position(0);
                    MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
                    audioInfo.set(bufferInfo.offset,bufferInfo.size,bufferInfo.presentationTimeUs,bufferInfo.flags);
                    if ((bufferInfo.flags & 0x03) == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        callbackConfig(audioBuffer, BufferInfo.createByBufferInfo(audioInfo));
                    } else {
                        callbackPartialFrame(audioBuffer,BufferInfo.createByBufferInfo(audioInfo));
                    }
                }
                audioEncoder.releaseOutputBuffer(outputBufferId, false);
            }

        }
    }

    private void callbackConfig(ByteBuffer encodeData, BufferInfo bufferInfo){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onAudioConfig(encodeData,bufferInfo);
            }
        }
    }

    private void callbackPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onAudioPartialFrame(encodeData,bufferInfo);
            }
        }
    }

    private void callbackFormatChange(AudioFormat audioFormat){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onAudioFormatChange(audioFormat);
            }
        }
    }

    @Override
    public void stop() {
        if (audioEncoder!=null){
            audioEncoder.stop();
            audioEncoder.release();
            audioEncoder = null;
            mediaFormat = null;
            audioBuffer = null;
        }
    }

    @Override
    public void release() {
        stop();
    }
}
