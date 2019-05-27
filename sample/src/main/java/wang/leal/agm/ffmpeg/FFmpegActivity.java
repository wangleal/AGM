package wang.leal.agm.ffmpeg;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
            tvInfo.setText("result:"+FFmpeg.execute("abc"));
        });
    }
}
