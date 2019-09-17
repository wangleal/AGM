package wang.leal.moment.recorder;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SyncVideoHardEncoder extends VideoHardEncoder {
    private static final String TAG = "SyncVideoHardEncoder";

    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding

    private Surface inputSurface;
    private MediaCodec videoEncoder;
    private MediaFormat mediaFormat;
    private MediaCodec.BufferInfo bufferInfo;
    private VideoFormat videoFormat;
    private ByteBuffer videoBuffer;

    @Override
    public Surface getInputSurface() {
        return inputSurface;
    }

    @Override
    public void start(VideoFormat videoFormat) {
        if (videoEncoder !=null){
            release();
        }
        this.videoFormat = videoFormat;
        bufferInfo = new MediaCodec.BufferInfo();
        mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, videoFormat.width, videoFormat.height);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, videoFormat.bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, videoFormat.fps);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, videoFormat.interval);
        try {
            videoEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            videoEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = videoEncoder.createInputSurface();
            videoEncoder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (videoEncoder !=null){
            videoEncoder.stop();
            videoEncoder.release();
            videoEncoder = null;
            bufferInfo = null;
            mediaFormat = null;
            videoBuffer = null;
        }
    }

    /**
     * Releases encoder resources.
     */
    public void release() {
        stop();
    }

    @Override
    public void draw() {
        super.draw();
        drainEncoder();
    }

    private void drainEncoder() {
        if (videoEncoder == null) {
            return;
        }
        final int TIMEOUT_USEC = 10000;

        ByteBuffer[] encoderOutputBuffers = videoEncoder.getOutputBuffers();
        while (true) {
            int outputIndex = videoEncoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "INFO_TRY_AGAIN_LATER");
                break;
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                encoderOutputBuffers = videoEncoder.getOutputBuffers();
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                videoFormat.format = videoEncoder.getOutputFormat();
                callbackFormatChange(videoFormat);
            } else if (outputIndex < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        outputIndex);
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[outputIndex];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + outputIndex +
                            " was null");
                }
                if (bufferInfo.size != 0) {
                    bufferInfo.presentationTimeUs = System.nanoTime()/1000;
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(bufferInfo.offset);
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);
                    byte[] bytes = new byte[bufferInfo.size];
                    encodedData.get(bytes,0,bufferInfo.size);
                    videoBuffer = ByteBuffer.allocateDirect(bufferInfo.size);
                    videoBuffer.put(bytes,0,bufferInfo.size);
                    videoBuffer.position(0);
                    MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
                    videoInfo.set(bufferInfo.offset,bufferInfo.size,bufferInfo.presentationTimeUs,bufferInfo.flags);
                    if ((videoInfo.flags & 0x01) == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                        callbackKeyFrame(videoBuffer, BufferInfo.createByBufferInfo(videoInfo));
                    } else if ((videoInfo.flags & 0x03) == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        callbackConfig(videoBuffer,BufferInfo.createByBufferInfo(videoInfo));
                    } else if ((videoInfo.flags & 0x07) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        callbackEnd(videoBuffer, BufferInfo.createByBufferInfo(videoInfo));
                    } else {
                        callbackPartialFrame(videoBuffer,BufferInfo.createByBufferInfo(videoInfo));
                    }
                }
                videoEncoder.releaseOutputBuffer(outputIndex, false);
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "end of stream reached");
                    break;      // out of while
                }
            }
        }
    }

    private void callbackConfig(ByteBuffer encodeData, BufferInfo bufferInfo){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onVideoConfig(encodeData,bufferInfo);
            }
        }
    }

    private void callbackKeyFrame(ByteBuffer encodeData, BufferInfo bufferInfo){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onVideoKeyFrame(encodeData,bufferInfo);
            }
        }
    }

    private void callbackPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onVideoPartialFrame(encodeData,bufferInfo);
            }
        }
    }

    private void callbackEnd(ByteBuffer encodeData, BufferInfo bufferInfo){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onVideoEnd(encodeData,bufferInfo);
            }
        }
    }

    private void callbackFormatChange(VideoFormat videoFormat){
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onVideoFormatChange(videoFormat);
            }
        }
    }
}
