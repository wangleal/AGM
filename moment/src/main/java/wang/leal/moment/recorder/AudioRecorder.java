package wang.leal.moment.recorder;

import java.util.ArrayList;
import java.util.List;

public abstract class AudioRecorder {
    protected List<Callback> callbacks = new ArrayList<>();

    public abstract void start(AudioFormat audioFormat);
    public abstract void stop();
    public abstract void release();

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
        void onAudioRecord(byte[] audioData, int size);
    }
}
