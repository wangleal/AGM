package wang.leal.agm.opus;

import android.content.Context;

import top.oply.opuslib.OpusService;

public class TopOpus implements IOpusService{
    @Override
    public void play(Context context, String filePath) {
        OpusService.play(context,filePath);
    }

    @Override
    public void pause(Context context) {
        OpusService.pause(context);
    }

    @Override
    public void stopPlay(Context context) {
        OpusService.stopPlaying(context);
    }

    @Override
    public void record(Context context, String filePath) {
        OpusService.record(context,filePath);
    }

    @Override
    public void stopRecord(Context context) {
        OpusService.stopRecording(context);
    }
}
