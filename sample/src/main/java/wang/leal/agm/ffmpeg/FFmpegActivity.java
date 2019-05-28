package wang.leal.agm.ffmpeg;

import android.Manifest;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;

import wang.leal.agm.R;

public class FFmpegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe();
        TextView tvInfo = findViewById(R.id.tv_info);
        tvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.bt_execute).setOnClickListener(v -> {
            String input = "/sdcard/DCIM/1558508952.mp4";
            String output = "/sdcard/123456789.mp4";
            File outputFile = new File(output);
            if (!outputFile.exists()){
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            tvInfo.setText("result:"+FFmpeg.execute("ffmpeg","-1",input,"-vf","crop=iw/2:ih/2",output));
            new Thread(){
                @Override
                public void run() {
                    int progress = FFmpeg.execute("ffmpeg","-i",input);
                    Log.e("FFmpegActivity","progress:"+progress);
                }
            }.start();
        });
    }
}
