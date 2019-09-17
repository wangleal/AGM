package wang.leal.moment.recorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;

public class AudioHardRecorder extends AudioRecorder {
    private static final String TAG = "AudioHardRecorder";
    private AudioRecord audioRecord;
    private boolean isRecording;

    private int getBufferSize(int sampleRateInHz,int channelConfig,int bytePerSample) {
        return AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, bytePerSample);
    }

    @Override
    public void start(AudioFormat audioFormat){
        try {
            int bufferSizeInBytes = getBufferSize(audioFormat.sampleRate,audioFormat.channelConfig,audioFormat.bytePerSample);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, audioFormat.sampleRate, audioFormat.channelConfig, audioFormat.bytePerSample, bufferSizeInBytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        new Thread(TAG){
            @Override
            public void run() {
                if (audioRecord==null){
                    return;
                }
                try {
                    AcousticEchoCanceler aec;
                    AutomaticGainControl agc;
                    if (AcousticEchoCanceler.isAvailable()) {
                        aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
                        if (aec != null) {
                            aec.setEnabled(true);
                        }
                    }
                    if (AutomaticGainControl.isAvailable()) {
                        agc = AutomaticGainControl.create(audioRecord.getAudioSessionId());
                        if (agc != null) {
                            agc.setEnabled(true);
                        }
                    }
                    isRecording = true;
                    audioRecord.startRecording();
                    int length = 2048;
                    byte[] audioData = new byte[length];
                    while (isRecording){
                        int size = audioRecord.read(audioData,0,length);
                        if (size>=0){
                            callbackOnCaptureData(audioData,length);
                        }else {
                            String message = size== AudioRecord.ERROR?"AudioRecorder.ERROR":size== AudioRecord.ERROR_BAD_VALUE?"AudioRecorder.ERROR_BAD_VALUE"
                                    :size== AudioRecord.ERROR_DEAD_OBJECT?"AudioRecorder.ERROR_DEAD_OBJECT"
                                    :size== AudioRecord.ERROR_INVALID_OPERATION?"AudioRecorder.ERROR_INVALID_OPERATION":"AudioRecorder.ERROR_OTHER";
                            throw new Exception(size+":"+message);
                        }
                    }
                    if (audioRecord!=null){
                        audioRecord.stop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    isRecording = false;
                }
            }
        }.start();
    }

    @Override
    public void stop(){
        if (audioRecord==null||audioRecord.getState()== AudioRecord.STATE_UNINITIALIZED){
            return;
        }
        try {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            isRecording = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        stop();
    }

    private void callbackOnCaptureData(byte[] audioData,int size){
        if (!isRecording){
            return;
        }
        if (callbacks!=null&&callbacks.size()>0){
            for (Callback callback:callbacks){
                callback.onAudioRecord(audioData,size);
            }
        }
    }
}
