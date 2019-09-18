package wang.leal.moment.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;

import androidx.constraintlayout.widget.ConstraintLayout;

import wang.leal.moment.R;
import wang.leal.moment.recorder.AudioFormat;
import wang.leal.moment.recorder.VideoFormat;
import wang.leal.moment.recorder.VideoRecorder;
import wang.leal.moment.view.ProgressView;

public class CameraView extends ConstraintLayout {

    private CameraRender cameraRender;
    private ProgressView progressView;
    private VideoRecorder videoRecorder;

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
        cameraRender = new CameraRender(textureView);
        videoRecorder = new VideoRecorder(getContext(), cameraRender);
        videoRecorder.setCallback(videoPath -> {
            if (this.callback!=null){
                this.callback.onRecordComplete(videoPath);
            }
        });
        progressView = findViewById(R.id.pv_action);
    }

    public void startCamera() {
        if (cameraRender != null) {
            cameraRender.startCamera();
        }
    }

    public void switchCamera() {
        if (cameraRender != null) {
            cameraRender.switchCamera();
        }
    }

    public void closeCamera() {
        if (cameraRender != null) {
            cameraRender.release();
        }
    }

    private boolean isStartRecord;

    private void startRecord() {
        isStartRecord = true;
        Log.e("Moment", "start record");
        if (videoRecorder!=null){
            videoRecorder.startRecord(VideoFormat.HW720, AudioFormat.SINGLE_CHANNEL_44100);
        }
    }

    private void stopRecord() {
        isStartRecord = false;
        Log.e("Moment", "stop record");
        if (videoRecorder!=null){
            videoRecorder.stopRecord();
        }
    }

    private void tackPhoto() {
        Log.e("Moment", "tack photo");
        if (cameraRender != null) {
            cameraRender.tackPhoto();
        }
    }

    private float oldDist = 1f;
    private float oldTouchY;//当并且touch Progress的时候
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
                    if (cameraRender != null) {
                        if (newDist > oldDist) {
                            cameraRender.handleZoom(true);
                        } else if (newDist < oldDist) {
                            cameraRender.handleZoom(false);
                        }
                    }
                    oldDist = newDist;
                }else if (count==1){
                    if (cameraRender != null&&actionPointer!=-1) {
                        float newY = event.getY();
                        if (newY<oldY){
                            cameraRender.handleZoom(true);
                        }else if (newY>oldY){
                            cameraRender.handleZoom(false);
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
                    oldY=oldTouchY=0;
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

    private Callback callback;
    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public interface Callback{
        void onRecordComplete(String filePath);
    }
}
