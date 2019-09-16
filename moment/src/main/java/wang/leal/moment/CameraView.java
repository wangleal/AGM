package wang.leal.moment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

    public void closeCamera(){
        if (textureRender!=null){
            textureRender.release();
        }
    }
}
