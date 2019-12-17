package wang.leal.agm.ffmpeg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FFmpeg {

    private static ExecutorService service = Executors.newSingleThreadExecutor();
    public static void execute(String[] cmds, Callback callback){
        service.execute(new CmdRunnable(cmds,callback));
    }

    public interface Callback{
        void onStart();
        void onProgress(float progress);
        void onEnd();
    }

    private static class CmdRunnable implements Runnable{
        private String[] cmds;
        private Callback callback;
        CmdRunnable(String[] cmds,Callback callback){
            this.cmds = cmds;
            this.callback = callback;
        }
        @Override
        public void run() {
            if (callback!=null){
                callback.onStart();
            }
            FFmpegCMD.setOnProgressListener(progress -> {
                if (callback!=null){
                    callback.onProgress(progress);
                }
            });
            FFmpegCMD.execute(cmds);
            if (callback!=null){
                callback.onEnd();
            }
            FFmpegCMD.setOnProgressListener(null);
        }
    }
}
