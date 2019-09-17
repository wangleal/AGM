package wang.leal.moment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;

import androidx.constraintlayout.widget.ConstraintLayout;

import wang.leal.moment.view.ProgressView;

public class CameraView extends ConstraintLayout {

    private TextureRender textureRender;
    private ProgressView progressView;

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

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_moment_camera, this);
        TextureView textureView = findViewById(R.id.texture_camera);
        textureRender = new TextureRender(textureView);
        progressView = findViewById(R.id.pv_action);
    }

    public void startCamera() {
        if (textureRender != null) {
            textureRender.startCamera();
        }
    }

    public void switchCamera() {
        if (textureRender != null) {
            textureRender.switchCamera();
        }
    }

    public void closeCamera() {
        if (textureRender != null) {
            textureRender.release();
        }
    }

    private boolean isStartRecord;

    private void startRecord() {
        isStartRecord = true;
        Log.e("Moment", "start record");
    }

    private void stopRecord() {
        isStartRecord = false;
        Log.e("Moment", "stop record");
    }

    private void tackPhoto() {
        Log.e("Moment", "tack photo");
        if (textureRender != null) {
            textureRender.tackPhoto();
        }
    }

    private float oldDist = 1f;
    private float oldTouchY;//当一个手指并且touch Progress的时候
    private float oldY;
    private int actionPointer = -1;//点击progress的手指
    private Handler handler = new Handler();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int count = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                float rawX, rawY;
                final int actionIndex = event.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int[] location = {0, 0};
                getLocationOnScreen(location);
                rawX = (int) event.getX(actionIndex) + location[0];
                rawY = (int) event.getY(actionIndex) + location[1];

                if (isTouchPointInView(rawX,rawY)){
                    if (actionPointer!=-1){
                        break;
                    }
                    oldY = oldTouchY = event.getY();
                    actionPointer = event.getPointerId(event.getActionIndex());
                    if (!isStartRecord){
                        handler.postDelayed(this::startRecord, 500);
                    }
                }else if (count>1&&!isTouchPointInView(rawX,rawY)){
                    oldDist = getFingerSpacing(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (count>1){
                    float newDist = getFingerSpacing(event);
                    if (textureRender != null) {
                        if (newDist > oldDist) {
                            textureRender.handleZoom(true);
                        } else if (newDist < oldDist) {
                            textureRender.handleZoom(false);
                        }
                    }
                    oldDist = newDist;
                }else if (count==1){
                    if (textureRender != null) {
                        float newY = event.getY();
                        if (newY<oldY){
                            textureRender.handleZoom(true);
                        }else if (newY>oldY){
                            textureRender.handleZoom(false);
                        }
                        if (newY>oldTouchY){
                            oldY = oldTouchY;
                        }else {
                            oldY = newY;
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerId(event.getActionIndex())==actionPointer){
                    if (!isStartRecord) {
                        handler.removeCallbacksAndMessages(null);
                        tackPhoto();
                    } else {
                        stopRecord();
                    }
                    actionPointer = -1;
                }
                break;
        }
        return true;
    }

    private boolean isTouchPointInView(float x, float y) {
        if (progressView == null) {
            return false;
        }
        int[] location = new int[2];
        progressView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + progressView.getMeasuredWidth();
        int bottom = top + progressView.getMeasuredHeight();
        return y >= top && y <= bottom && x >= left
                && x <= right;
    }

    private static float getFingerSpacing(MotionEvent event) {
        int count = event.getPointerCount();
        if (count < 2) {
            return 0;
        }
        float x = event.getX(count - 2) - event.getX(count - 1);
        float y = event.getY(count - 2) - event.getY(count - 1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
