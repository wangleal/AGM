package wang.leal.agm.opus;

import android.content.Context;

interface IOpusService {

    void play(Context context,String filePath);
    void pause(Context context);
    void stopPlay(Context context);

    void record(Context context,String filePath);
    void stopRecord(Context context);


}
