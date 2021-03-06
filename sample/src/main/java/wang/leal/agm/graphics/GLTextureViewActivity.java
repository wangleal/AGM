package wang.leal.agm.graphics;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import wang.leal.agm.R;

public class GLTextureViewActivity extends AppCompatActivity {
    private CameraGLTextureRender cameraRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_texture);
        initView();
    }

    private void initView(){
        GLTextureView textureView = findViewById(R.id.tv_gl_texture);
        textureView.setOutlineProvider(new TextureVideoViewOutlineProvider(20));
        textureView.setClipToOutline(true);
        cameraRender = new CameraGLTextureRender(textureView);
        cameraRender.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraRender!=null){
            cameraRender.release();
        }
    }

    public class TextureVideoViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public TextureVideoViewOutlineProvider(float radius) {
            this.mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            int leftMargin = 0;
            int topMargin = 0;
            Rect selfRect = new Rect(leftMargin, topMargin,
                    rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
            outline.setRoundRect(selfRect, mRadius);
        }
    }
}
