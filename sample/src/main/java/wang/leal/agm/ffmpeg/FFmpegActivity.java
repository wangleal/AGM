package wang.leal.agm.ffmpeg;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import wang.leal.agm.R;

public class FFmpegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);
        TextView tvInfo = findViewById(R.id.tv_info);
        tvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.bt_execute).setOnClickListener(v -> {
            String[] cmd = {
                    "ffmpeg",
                    "-i", "/sdcard/1576481874.mp4",
                    "-vf", "select='eq(pict_type\\,I)'",
                    "-vsync", " 2",
                    "-f", "image2", "/sdcard/core-%02d.jpeg ",
            };
            for (int i=0;i<10;i++){
                int finalI = i;
                FFmpeg.execute(cmd, new FFmpeg.Callback() {
                    @Override
                    public void onStart() {
                        Log.e("FFmpeg","index:"+ finalI +",start");
                    }

                    @Override
                    public void onProgress(float progress) {
                        Log.e("FFmpeg","index:"+ finalI +",progress:"+progress);
                    }

                    @Override
                    public void onEnd() {
                        Log.e("FFmpeg","index:"+ finalI +",end");
                    }
                });
            }
        });
    }
}
