package wang.leal.agm.opus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import wang.leal.agm.R;

public class OpusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opus);
        String filePath = getExternalCacheDir().getAbsolutePath()+"/a.ogg";
        File opus = new File(filePath);
        if (!opus.exists()){
            try {
                opus.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        findViewById(R.id.bt_play).setOnClickListener(v -> Opus.play(getApplicationContext(),filePath,null));
        findViewById(R.id.bt_stop).setOnClickListener(v -> Opus.stopPlay(getApplicationContext()));
        findViewById(R.id.bt_record).setOnClickListener(v -> Opus.record(getApplicationContext(),filePath));
        findViewById(R.id.bt_stop_record).setOnClickListener(v -> Opus.stopRecord(getApplicationContext()));
    }
}
