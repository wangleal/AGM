package wang.leal.moment.recorder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Audio encoder interface.
 */
public abstract class AudioEncoder {
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
        void onAudioConfig(ByteBuffer encodeData, BufferInfo bufferInfo);
        void onAudioPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo);
        void onAudioFormatChange(AudioFormat audioFormat);
    }

    public abstract void start(AudioFormat audioFormat);
    public abstract void write(byte[] audioData,int size);
    public abstract void stop();
    public abstract void release();
}
