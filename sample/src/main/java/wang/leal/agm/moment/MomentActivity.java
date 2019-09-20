package wang.leal.agm.moment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import wang.leal.agm.R;
import wang.leal.moment.camera.CameraView;
import wang.leal.moment.editor.EditorView;

public class MomentActivity extends AppCompatActivity {
    private  CameraView cameraView;
    private  EditorView editorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHalfTransparent();
        initView();
    }

    private void setHalfTransparent(){
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_moment);
    }

    private void initView(){
        cameraView = findViewById(R.id.cv_moment_camera);
        requestPermission();
        editorView = findViewById(R.id.ev_moment_play);
        cameraView.setCallback(new CameraView.Callback() {
            @Override
            public void onPhotoComplete(Bitmap bitmap) {
                cameraView.post(() -> {
                    if (editorView!=null){
                        editorView.setVisibility(View.VISIBLE);
                        editorView.showPhoto(bitmap);
                    }
                });
            }

            @Override
            public void onRecordComplete(String filePath) {
                cameraView.post(() -> {
                    if (editorView!=null){
                        editorView.setVisibility(View.VISIBLE);
                        editorView.startPlay(filePath);
                    }
                });
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1000);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1000);
            }
        }else {
            cameraView.startCamera();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1001);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView.startCamera();
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        if (editorView.getVisibility()==View.VISIBLE){
            editorView.stopPlay();
            editorView.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView!=null){
            cameraView.closeCamera();
        }
    }
}
