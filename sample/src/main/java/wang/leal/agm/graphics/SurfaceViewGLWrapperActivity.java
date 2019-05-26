package wang.leal.agm.graphics;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import wang.leal.agm.R;

public class SurfaceViewGLWrapperActivity extends AppCompatActivity {

    private CameraWrapperSurfaceRender cameraRender;
    private SurfaceViewGLWrapper surfaceViewGLWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_surface);
        initView();
    }

    private void initView(){
        SurfaceView surfaceView = findViewById(R.id.tv_gl_surface);
        surfaceView.setOutlineProvider(new SurfaceViewOutlineProvider(20));
        surfaceView.setClipToOutline(true);
        surfaceViewGLWrapper = new SurfaceViewGLWrapper(surfaceView);
        cameraRender = new CameraWrapperSurfaceRender(surfaceView){
            @Override
            protected void requestRender() {
                surfaceViewGLWrapper.requestRender();
            }
        };
        surfaceViewGLWrapper.setRenderer(cameraRender);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraRender!=null){
            cameraRender.release();
        }
    }

    public class SurfaceViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public SurfaceViewOutlineProvider(float radius) {
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
