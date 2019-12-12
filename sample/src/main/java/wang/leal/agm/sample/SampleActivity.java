package wang.leal.agm.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import wang.leal.agm.R;
import wang.leal.agm.camera.CameraActivity;
import wang.leal.agm.ffmpeg.FFmpegActivity;
import wang.leal.agm.opus.OpusActivity;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initView();
    }

    private void initView(){
        findViewById(R.id.bt_ffmpeg).setOnClickListener(v->startActivity(new Intent(this, FFmpegActivity.class)));
        findViewById(R.id.bt_opus).setOnClickListener(v->startActivity(new Intent(this, OpusActivity.class)));
        findViewById(R.id.bt_camera).setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));
    }
}
