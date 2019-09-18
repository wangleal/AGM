package wang.leal.moment.editor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;

import wang.leal.moment.R;

public class EditorView extends ConstraintLayout {

    private  VideoView videoView;
    public EditorView(Context context) {
        super(context);
        initView();
    }

    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_moment_editor, this);
        videoView = findViewById(R.id.vv_player);
        initVideoView();
    }

    private void initVideoView(){
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int videoHeight;
        int mode;
        if (screenHeight/screenWidth>=2){
            videoHeight = screenWidth*2;
            mode = MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
        }else if (screenHeight*1.0f/screenWidth>=16f/9){
            videoHeight = screenWidth*16/9;
            mode = MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT;
        }else {
            videoHeight = screenWidth*16/9;
            mode = MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
        }
        ConstraintLayout.LayoutParams videoParams = (LayoutParams) videoView.getLayoutParams();
        videoParams.width = screenWidth;
        videoParams.height = videoHeight;
        videoView.setLayoutParams(videoParams);
        videoView.setOnPreparedListener(mp -> {
            mp.setVideoScalingMode(mode);
            mp.setLooping(true);
            mp.start();
        });
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                videoView.setAlpha(1);
                setBackgroundColor(Color.BLACK);
            }
            return false;
        });
    }

    public void startPlay(String filePath){
        videoView.setAlpha(0);
        setBackgroundColor(Color.TRANSPARENT);
        videoView.setVideoPath(filePath);
    }

    public void stopPlay(){
        videoView.stopPlayback();
        videoView.pause();
    }

    public void release(){
    }
}
