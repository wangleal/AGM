package wang.leal.moment.recorder;

import android.os.Handler;
import android.os.HandlerThread;

public class MicEncoder implements AudioRecorder.Callback {
    private static final String TAG = "MicEncoder";
    private AudioEncoder audioEncoder;
    private HandlerThread handlerThread;
    private Handler handler;
    private boolean isStart = false;
    private boolean isVoice = true;
    private byte[] muteBuffer = new byte[2048];
    public MicEncoder(){
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(()->audioEncoder = new AudioHardEncoder());
    }

    public void start(AudioFormat audioFormat){
        handler.post(()->{
            audioEncoder.start(audioFormat);
            isStart = true;
        });
    }

    public void stop(){
        isStart = false;
        handler.post(()->{
            audioEncoder.stop();
            audioEncoder.release();
        });
    }

    public void release(){
        isStart = false;
        handler.post(()->{
            audioEncoder.removeCallback(null);
            handlerThread.quitSafely();
            handlerThread = null;
            handler.removeCallbacksAndMessages(null);
        });
    }

    public void addCallback(final AudioEncoder.Callback callback){
        handler.post(()->audioEncoder.addCallback(callback));
    }

    public void removeCallback(final AudioEncoder.Callback callback){
        handler.post(()->audioEncoder.removeCallback(callback));
    }

    public void voice(){
        isVoice = true;
    }

    public void mute(){
        isVoice = false;
    }

    @Override
    public void onAudioRecord(final byte[] audioData,int size) {
        if (isStart) {
            handler.post(()->{
                if (isVoice){
                    audioEncoder.write(audioData,size);
                }else {
                    audioEncoder.write(muteBuffer,size);
                }
            });
        }
    }

}