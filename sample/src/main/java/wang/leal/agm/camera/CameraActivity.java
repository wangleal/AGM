package wang.leal.agm.camera;

import android.os.Bundle;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import wang.leal.agm.R;
import wang.leal.agm.camera.preview.CameraPreview;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        TextureView cameraView = findViewById(R.id.tv_texture);
        CameraPreview cameraPreview = new CameraPreview(cameraView);
        cameraPreview.setOnPreviewListener(cameraPreview::startPreview);
    }
}
