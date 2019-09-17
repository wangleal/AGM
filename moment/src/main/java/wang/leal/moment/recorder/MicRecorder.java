package wang.leal.moment.recorder;

import android.os.Handler;
import android.os.HandlerThread;

public class MicRecorder {
    private static final String TAG = "MicRecorder";
    private AudioRecorder audioRecorder;
    private HandlerThread handlerThread;
    private Handler handler;
    public MicRecorder(){
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(()->audioRecorder = AudioRecordManager.getAudioRecord(AudioRecordManager.TYPE_AUDIO_MIC));
    }

    public void start(AudioFormat audioFormat){
        handler.post(()->audioRecorder.start(audioFormat));
    }

    public void stop(){
        handler.post(()->{
            audioRecorder.stop();
            audioRecorder.release();
        });
    }

    public void release(){
       handler.post(()->{
           audioRecorder.removeCallback(null);
           handlerThread.quitSafely();
           handlerThread = null;
           handler.removeCallbacksAndMessages(null);
       });
    }

    /**
     * Add callback
     * @param callback  callback
     */
    public void addCallback(final AudioRecorder.Callback callback){
        handler.post(()->audioRecorder.addCallback(callback));
    }

    /**
     * Remove callback
     * @param callback  If is null,remove all callback.
     */
    public void removeCallback(final AudioRecorder.Callback callback){
        handler.post(()->audioRecorder.removeCallback(callback));
    }
}
