package wang.leal.moment.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import wang.leal.moment.R;
import wang.leal.moment.recorder.AudioFormat;
import wang.leal.moment.recorder.VideoFormat;
import wang.leal.moment.recorder.VideoRecorder;
import wang.leal.moment.view.ProgressView;

public class CameraView extends ConstraintLayout {

    private CameraRender cameraRender;
    private ProgressView progressView;
    private ImageView ivLock;
    private VideoRecorder videoRecorder;
    private boolean isLock = false;

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
        setClipChildren(false);
        TextureView textureView = findViewById(R.id.texture_camera);
        cameraRender = new CameraRender(textureView);
        videoRecorder = new VideoRecorder(getContext(), cameraRender);
        videoRecorder.setCallback(videoPath -> {
            if (this.callback != null) {
                this.callback.onRecordComplete(videoPath);
            }
        });
        ivLock = findViewById(R.id.iv_lock);
        progressView = findViewById(R.id.pv_action);
        progressView.setCallback(this::stopRecord);
        findViewById(R.id.iv_switch).setOnClickListener(v -> switchCamera());
    }

    public void startCamera() {
        if (cameraRender != null) {
            cameraRender.startCamera();
        }
    }

    private void switchCamera() {
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
    private long startTime;//用来处理1秒之内的录取，1秒以内的强制录1秒

    private void startRecord() {
        isStartRecord = true;
        Log.e("Moment", "start record");
        if (videoRecorder != null) {
            videoRecorder.startRecord(VideoFormat.HW720, AudioFormat.SINGLE_CHANNEL_44100);
        }
        if (progressView != null) {
            progressView.showRecord();
        }
        ivLock.setVisibility(VISIBLE);
        startTime = System.currentTimeMillis();
    }

    private void stopRecord() {
        isStartRecord = false;
        Log.e("Moment", "stop record");
        if (videoRecorder != null) {
            videoRecorder.stopRecord();
        }
        ivLock.setVisibility(GONE);
        isLock = false;
        isLockPress = false;
        startTime = 0;
        actionPointer = -1;
    }

    private void tackPhoto() {
        Log.e("Moment", "tack photo");
        if (cameraRender != null) {
            cameraRender.takePhoto(bitmap -> {
                if (callback != null) {
                    callback.onPhotoComplete(bitmap);
                }
                if (progressView != null) {
                    progressView.showDefault();
                }
            });
        }

    }

    private float oldDist = 1f;
    private float oldTouchY;//当并且touch Progress的时候
    private float oldY;
    private int actionPointer = -1;//点击progress的手指
    private Handler handler = new Handler();
    private boolean isLockPress = false;//锁住状态点击状态

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int count = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (isTouchProgress(event)) {
                    if (isLock) {//锁住之后，再次点击action view，意思是结束
                        isLockPress = true;
                    } else {
                        if (actionPointer != -1) {
                            break;
                        }
                        oldY = oldTouchY = event.getY();
                        actionPointer = event.getPointerId(event.getActionIndex());
                        if (!isStartRecord) {
                            handler.postDelayed(this::startRecord, 500);
                            if (progressView != null) {
                                progressView.showTransition();
                            }
                        }
                    }
                } else {
                    if (count > 1) {//当手指数量大于1，才开始计算两指缩放
                        oldDist = getFingerSpacing(event);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTouchProgress(event)) {
                    if (count > 1) {
                        float newDist = getFingerSpacing(event);
                        if (cameraRender != null) {
                            if (newDist > oldDist) {
                                cameraRender.handleZoom(true);
                            } else if (newDist < oldDist) {
                                cameraRender.handleZoom(false);
                            }
                        }
                        oldDist = newDist;
                    } else if (count == 1) {
                        if (cameraRender != null && actionPointer != -1) {
                            float newY = event.getY();
                            if (newY < oldY) {
                                cameraRender.handleZoom(true);
                            } else if (newY > oldY) {
                                cameraRender.handleZoom(false);
                            }
                            if (newY > oldTouchY) {
                                oldY = oldTouchY;
                            } else {
                                oldY = newY;
                            }
                        }
                    }
                }
                if (ivLock.getVisibility() == VISIBLE && isTouchLock(event)) {//检测是否滑中lock
                    isLock = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (isLock) {
                    if (isLockPress) {
                        if (isTouchProgress(event)) {
                            if (progressView != null) {
                                progressView.showDefault();
                            }
                            long diffTime = System.currentTimeMillis() - startTime;
                            if (diffTime >= 1000 && diffTime < 15 * 1000) {
                                stopRecord();
                            } else {
                                handler.postDelayed(this::stopRecord, 1000 - diffTime);
                            }
                        }
                    } else {
                        if (progressView != null) {
                            progressView.showLock();
                        }
                    }
                } else {
                    if (event.getPointerId(event.getActionIndex()) == actionPointer) {
                        if (!isStartRecord) {
                            handler.removeCallbacksAndMessages(null);
                            tackPhoto();
                        } else {
                            if (progressView != null) {
                                progressView.showDefault();
                            }
                            long diffTime = System.currentTimeMillis() - startTime;
                            if (diffTime >= 1000 && diffTime < 15 * 1000) {
                                stopRecord();
                            } else {
                                handler.postDelayed(this::stopRecord, 1000 - diffTime);
                            }
                        }
                        oldY = oldTouchY = 0;
                        actionPointer = -1;
                    }
                }
                break;
        }
        return true;
    }

    private boolean isTouchProgress(MotionEvent event) {
        float rawX, rawY;
        final int[] location = {0, 0};
        final int actionIndex = event.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        getLocationOnScreen(location);
        rawX = (int) event.getX(actionIndex) + location[0];
        rawY = (int) event.getY(actionIndex) + location[1];
        return isTouchPointInView(rawX, rawY, progressView);
    }

    private boolean isTouchLock(MotionEvent event) {
        float rawX, rawY;
        final int[] location = {0, 0};
        final int actionIndex = event.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        getLocationOnScreen(location);
        rawX = (int) event.getX(actionIndex) + location[0];
        rawY = (int) event.getY(actionIndex) + location[1];
        return isTouchPointInView(rawX, rawY, ivLock);
    }

    private boolean isTouchPointInView(float x, float y, View view) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
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

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onPhotoComplete(Bitmap bitmap);

        void onRecordComplete(String filePath);
    }
}
