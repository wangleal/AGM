package wang.leal.moment.editor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.IOException;

import wang.leal.moment.R;
import wang.leal.moment.recorder.AudioFormat;
import wang.leal.moment.recorder.VideoFormat;
import wang.leal.moment.transcoder.MediaTranscoder;
import wang.leal.moment.transcoder.format.MediaFormatStrategyPresets;

public class EditorView extends ConstraintLayout {

    private static final String TAG = "EditorView";
    private VideoView videoView;

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

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_moment_editor, this);
        videoView = findViewById(R.id.vv_player);
        initVideoView();
    }

    private void initVideoView() {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int videoHeight;
        int mode;
        if (screenHeight / screenWidth >= 2) {
            videoHeight = screenWidth * 2;
            mode = MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
        } else if (screenHeight * 1.0f / screenWidth >= 16f / 9) {
            videoHeight = screenWidth * 16 / 9;
            mode = MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT;
        } else {
            videoHeight = screenWidth * 16 / 9;
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
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                videoView.setAlpha(1);
                setBackgroundColor(Color.BLACK);
            }
            return false;
        });
        findViewById(R.id.bt_transcoder).setOnClickListener(v -> {
            transcoder();
        });
    }

    private String filePath;
    public void startPlay(String filePath) {
        this.filePath = filePath;
        videoView.setAlpha(0);
        setBackgroundColor(Color.TRANSPARENT);
        videoView.setVideoPath(filePath);
    }

    public void stopPlay() {
        videoView.stopPlayback();
        videoView.pause();
    }

    private void transcoder() {
        final File file;
        try {
            File outputDir = new File(getContext().getExternalFilesDir(null), "outputs");
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdir();
            file = File.createTempFile("transcode_test", ".mp4", outputDir);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create temporary file.", e);
            Toast.makeText(getContext(), "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return;
        }
        final long startTime = SystemClock.uptimeMillis();
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
            }

            @Override
            public void onTranscodeCompleted() {
                Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                getContext().startActivity(new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.parse(file.getAbsolutePath()), "video/mp4")
                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
            }

            @Override
            public void onTranscodeCanceled() {
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
            }
        };
        Log.d(TAG, "transcoding into " + file);
        try {
            MediaTranscoder.getInstance().transcodeVideo(filePath, file.getAbsolutePath(),
                    MediaFormatStrategyPresets.createAndroid720pStrategy(VideoFormat.HW720.bitrate, AudioFormat.SINGLE_CHANNEL_44100.bitrate, AudioFormat.SINGLE_CHANNEL_44100.channelCount), listener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void release() {
    }
}
