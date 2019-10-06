package wang.leal.agm.opus;

import android.content.Context;

public class Opus {

    private static final IOpusService opusService = new TopOpus();

    public static void play(Context context, String filePath, PlayCallback playCallback){
        opusService.play(context,filePath, playCallback);
    }
    public static void pause(Context context){
        opusService.pause(context);
    }
    public static void stopPlay(Context context){
        opusService.stopPlay(context);
    }

    public static void record(Context context,String filePath){
        opusService.record(context,filePath);
    }
    public static void stopRecord(Context context){
        opusService.stopRecord(context);
    }

}
