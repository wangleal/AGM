package wang.leal.moment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CameraView extends ConstraintLayout {

    private TextureRender textureRender;

    public CameraView(Context context) {
        super(context);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_moment_camera,this);
        TextureView textureView = findViewById(R.id.texture_camera);
        textureRender = new TextureRender(textureView);
    }

    public void startCamera(){
        if (textureRender!=null){
            textureRender.startCamera();
        }
    }

    public void switchCamera(){
        if (textureRender!=null){
            textureRender.switchCamera();
        }
    }

    public void closeCamera(){
        if (textureRender!=null){
            textureRender.release();
        }
    }

    private float oldDist = 1f;
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    if (textureRender!=null){
                        if (newDist > oldDist) {
                            textureRender.handleZoom(true);
                        } else if (newDist < oldDist) {
                            textureRender.handleZoom(false);
                        }
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
