package wang.leal.agm.graphics;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import wang.leal.agm.R;

public class TextureViewGLWrapperActivity extends AppCompatActivity {

    private CameraWrapperTextureRender cameraRender;
    private TextureViewGLWrapper textureViewGLWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_texture);
        initView();
    }

    private void initView(){
        TextureView textureView = findViewById(R.id.tv_gl_texture);
        textureView.setOutlineProvider(new TextureVideoViewOutlineProvider(20));
        textureView.setClipToOutline(true);
        textureViewGLWrapper = new TextureViewGLWrapper(textureView);
        cameraRender = new CameraWrapperTextureRender(textureView){
            @Override
            protected void requestRender() {
                textureViewGLWrapper.requestRender();
            }
        };
        textureViewGLWrapper.setRenderer(cameraRender);
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
