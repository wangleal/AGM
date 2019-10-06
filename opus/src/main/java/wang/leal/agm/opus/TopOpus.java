package wang.leal.agm.opus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import top.oply.opuslib.OpusService;

public class TopOpus implements IOpusService{

    @Override
    public void play(Context context, String filePath,PlayCallback playCallback) {
        PlayReceiver playReceiver = new PlayReceiver(context,playCallback);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("top.oply.oplayer.action.ui_receiver");
        context.registerReceiver(playReceiver, intentFilter);  // 注册广播接收器
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


    static class PlayReceiver extends BroadcastReceiver{
        Context context;
        PlayCallback playCallback;
        PlayReceiver(Context context,PlayCallback playCallback){
            this.context = context;
            this.playCallback = playCallback;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle!=null){
                int type = bundle.getInt("EVENT_TYPE");
                if (type==1001){
                    if (playCallback!=null){
                        playCallback.onPlayComplete();
                    }
                    context.unregisterReceiver(this);
                }
            }
        }
    }

}
