package wang.leal.moment.recorder;

import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Video encoder interface.
 */
public abstract class VideoEncoder {

    protected List<Callback> callbacks = new ArrayList<>();
    /**
     * Add callback
     * @param callback  callback
     */
    public void addCallback(Callback callback){
        if (callback==null){
            return;
        }
        callbacks.add(callback);
    }

    /**
     * Remove callback
     * @param callback  If is null,remove all callback.
     */
    public void removeCallback(Callback callback){
        if (callback==null){
            callbacks.clear();
        }else {
            callbacks.remove(callback);
        }
    }

    public interface Callback{
        void onVideoConfig(ByteBuffer encodeData, BufferInfo bufferInfo);
        void onVideoKeyFrame(ByteBuffer encodeData, BufferInfo bufferInfo);
        void onVideoPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo);
        void onVideoEnd(ByteBuffer encodeData, BufferInfo bufferInfo);
        void onVideoFormatChange(VideoFormat videoFormat);
    }
    /**
     * Returns the encoder's input surface.
     */
    public Surface getInputSurface(){
        return null;
    }
    public void draw(){

    }

    public void write(){

    }
    public abstract void start(VideoFormat videoFormat);
    public abstract void stop();
    public abstract void release();
}
