package wang.leal.agm.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import wang.leal.agm.R;
import wang.leal.agm.graphics.SurfaceViewGLWrapperActivity;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initView();
    }

    private void initView(){
        findViewById(R.id.bt_gl_texture).setOnClickListener(v->startActivity(new Intent(this, SurfaceViewGLWrapperActivity.class)));
    }
}
