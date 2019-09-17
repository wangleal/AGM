package wang.leal.moment.recorder;

public class AudioRecordManager {
    public static final int TYPE_AUDIO_MIC = 1;

    public static AudioRecorder getAudioRecord(int type){
        return new AudioHardRecorder();
    }
}
