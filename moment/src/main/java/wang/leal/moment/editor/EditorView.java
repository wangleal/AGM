package wang.leal.moment.editor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import wang.leal.moment.R;
import wang.leal.moment.friend.FriendView;
import wang.leal.moment.recorder.AudioFormat;
import wang.leal.moment.recorder.VideoFormat;
import wang.leal.moment.transcoder.ImageTranscoder;
import wang.leal.moment.transcoder.MediaTranscoder;
import wang.leal.moment.transcoder.format.MediaFormatStrategyPresets;
import wang.leal.moment.view.TextLayout;

public class EditorView extends ConstraintLayout {

    private VideoView videoView;
    private ImageView ivPhoto;
    private ImageView ivSave;
    private ImageView ivSend;
    private ImageView ivBack;
    private TextLayout textLayout;
    private Bitmap coverBitmap;
    private ImageView ivText;
    private FriendView friendView;

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
        ivPhoto = findViewById(R.id.iv_photo);
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
                showView();
            }
            return false;
        });
        videoView.setOnClickListener(v -> showEdit());
        ivPhoto.setOnClickListener(v -> showEdit());
        ivSend = findViewById(R.id.iv_send);
        ivSend.setOnClickListener(v -> sendToFriend());
        ivSave = findViewById(R.id.iv_save_media);
        ivSave.setOnClickListener(v -> {
            if (ivPhoto.getVisibility()==VISIBLE){
                savePhoto();
            }else {
                transcoder();
            }
        });
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            stopPlay();
            setVisibility(GONE);
        });
        textLayout = findViewById(R.id.tl_text_layout);
        ivText = findViewById(R.id.iv_text);
        ivText.setOnClickListener(v -> showEdit());
        setOnClickListener(v -> {});
        textLayout.setCallback(new TextLayout.Callback() {
            @Override
            public void onShow() {
                if (ivSave!=null){
                    ivSave.setVisibility(VISIBLE);
                }
                if (ivSend!=null){
                    ivSend.setVisibility(VISIBLE);
                }
                if (ivBack!=null){
                    ivBack.setVisibility(VISIBLE);
                }
                if (ivText!=null){
                    ivText.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onGone() {
                if (ivSave!=null){
                    ivSave.setVisibility(INVISIBLE);
                }
                if (ivSend!=null){
                    ivSend.setVisibility(INVISIBLE);
                }
                if (ivBack!=null){
                    ivBack.setVisibility(INVISIBLE);
                }
                if (ivText!=null){
                    ivText.setVisibility(INVISIBLE);
                }
            }
        });
    }

    private void sendToFriend(){
        if (friendView==null){
            friendView = new FriendView(getContext());
            addView(friendView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        }
        Bitmap bitmap = null;
        if (ivPhoto.getVisibility()==VISIBLE){
            bitmap = getPhoto();
        }else {
            bitmap = getVideoBitmap();
        }
        if (bitmap==null){
            Toast.makeText(getContext(),"图片生成失败",Toast.LENGTH_LONG).show();
            return;
        }
        friendView.show(ImageTranscoder.mosaic(getContext(),bitmap));
        ivSave.performClick();
    }

    private void showEdit(){
        if (textLayout!=null){
            textLayout.showEdit();
        }
    }

    private void showView(){
        ivSend.setVisibility(VISIBLE);
        ivSave.setVisibility(VISIBLE);
        textLayout.showCover(coverBitmap);
    }

    public void showPhoto(Bitmap bitmap,Bitmap coverBitmap){
        this.coverBitmap = coverBitmap;
        showMenu();
        ivPhoto.setVisibility(VISIBLE);
        ivPhoto.setImageBitmap(bitmap);
        showView();
    }

    private String filePath;
    public void startPlay(String filePath,Bitmap coverBitmap) {
        this.coverBitmap = coverBitmap;
        showMenu();
        ivPhoto.setVisibility(GONE);
        ivSend.setVisibility(GONE);
        ivSave.setVisibility(GONE);
        this.filePath = filePath;
        videoView.setAlpha(0);
        setBackgroundColor(Color.TRANSPARENT);
        videoView.setVideoPath(filePath);
    }

    public void stopPlay() {
        videoView.stopPlayback();
        videoView.pause();
    }

    private void showMenu(){
        if(coverBitmap==null){
            ivText.setImageResource(R.drawable.ic_camera_editor_text);
            ivBack.setImageResource(R.drawable.ic_camera_editor_back);
            ivSave.setImageResource(R.drawable.ic_camera_editor_save);
            ivSave.setTag(0);
            ivSend.setImageResource(R.drawable.ic_camera_editor_send);
        }else {
            ivText.setImageResource(R.drawable.ic_camera_editor_text_cover);
            ivBack.setImageResource(R.drawable.ic_camera_editor_back_cover);
            ivSave.setImageResource(R.drawable.ic_camera_editor_save_cover);
            ivSave.setTag(1);
            ivSend.setImageResource(R.drawable.ic_camera_editor_send_cover);
        }
    }

    private void savePhoto(){
        try {
            Bitmap bitmap = getPhoto();
            if (bitmap==null){
                Toast.makeText(getContext(),"保存失败",Toast.LENGTH_LONG).show();
                return;
            }
            saveToFile(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap getPhoto(){
        try {
            textLayout.setDrawingCacheEnabled(true);
            textLayout.buildDrawingCache();
            waterMark = textLayout.getDrawingCache();
            Drawable drawable = ivPhoto.getDrawable();
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable){
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap sourceBitmap = bitmapDrawable.getBitmap();
                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(sourceBitmap, 0, 0, null);
                canvas.drawBitmap(waterMark,0,0,null);
                canvas.save();
            }
            textLayout.destroyDrawingCache();
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getVideoBitmap(){
        try {
            textLayout.setDrawingCacheEnabled(true);
            textLayout.buildDrawingCache();
            waterMark = textLayout.getDrawingCache();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);

            Bitmap sourceBitmap = retriever.getFrameAtTime();
            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(sourceBitmap, 0, 0, null);
            canvas.drawBitmap(waterMark,0,0,null);
            canvas.save();
            textLayout.destroyDrawingCache();
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void saveToFile(Bitmap bitmap){
        if (bitmap==null){
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    File photoFile = getPhotoFile();
                    if (!photoFile.exists()) {
                        photoFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    insertPhotoToMediaStore(photoFile);
                    if (friendView!=null&&friendView.getVisibility()==VISIBLE){
                        friendView.setFilePath(photoFile.getAbsolutePath(),"image/jpeg");
                    }
                    post(() -> showSaveSuccess());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void showSaveSuccess(){
        if ((int)ivSave.getTag()==0){
            ivSave.setImageResource(R.drawable.ic_camera_editor_save_success);
        }else {
            ivSave.setImageResource(R.drawable.ic_camera_editor_save_success_cover);
        }
    }

    public static Bitmap waterMark = null;
    private void transcoder() {
        File transcoderFile = getTranscoderFile();
        Log.e("EditorView","transcode file:"+transcoderFile.getAbsolutePath());
        final long startTime = SystemClock.uptimeMillis();
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
            }

            @Override
            public void onTranscodeCompleted() {
                Log.e("EditorView","transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                Log.e("EditorView","complete file:"+transcoderFile.getAbsolutePath());
                insertVideoToMediaStore(transcoderFile);
                if (friendView!=null&&friendView.getVisibility()==VISIBLE){
                    friendView.setFilePath(transcoderFile.getAbsolutePath(),"video/mp4");
                }
                post(() -> showSaveSuccess());
            }

            @Override
            public void onTranscodeCanceled() {
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
            }
        };
        Log.e("EditorView", "transcoding into " + transcoderFile.getAbsolutePath());
        try {
            textLayout.setDrawingCacheEnabled(true);
            textLayout.buildDrawingCache();
            waterMark = textLayout.getDrawingCache();
            if (waterMark!=null){
                waterMark = centerCrop(waterMark,720,1280);
                Log.e("EditorView","bitmap width:"+waterMark.getWidth()+",height:"+waterMark.getHeight()+",bitmap:"+waterMark);
            }
            textLayout.destroyDrawingCache();
            MediaTranscoder.getInstance().transcodeVideo(filePath, transcoderFile.getAbsolutePath(),
                    MediaFormatStrategyPresets.createAndroid720pStrategy(VideoFormat.HW720.bitrate, AudioFormat.SINGLE_CHANNEL_44100.bitrate, AudioFormat.SINGLE_CHANNEL_44100.channelCount), listener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private File getTranscoderFile() {
        File rootFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (rootFileDir==null){
            throw new RuntimeException("Default root dir is null.May not be authorized.");
        }
        if (!rootFileDir.exists()){
            rootFileDir.mkdirs();
        }
        String filePath = rootFileDir.getAbsolutePath()+"/"+System.currentTimeMillis()/1000+".mp4";
        File file = new File(filePath);
        if (file.exists()){
            file.deleteOnExit();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private File getPhotoFile() {
        File rootFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (rootFileDir==null){
            throw new RuntimeException("Default root dir is null.May not be authorized.");
        }
        if (!rootFileDir.exists()){
            rootFileDir.mkdirs();
        }
        String filePath = rootFileDir.getAbsolutePath()+"/"+System.currentTimeMillis()/1000+".jpg";
        File file = new File(filePath);
        if (file.exists()){
            file.deleteOnExit();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void insertPhotoToMediaStore(File sourceFile){

        ContentValues newValues = new ContentValues(6);
        String title = sourceFile.getName();
        newValues.put(MediaStore.Images.Media.TITLE,title);
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME,
                sourceFile.getName());
        newValues.put(MediaStore.Images.Media.DATA, sourceFile.getPath());
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED,
                System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        localIntent.setData(uri);
        getContext().sendBroadcast(localIntent);
    }

    private void insertVideoToMediaStore(File sourceFile){

        ContentValues newValues = new ContentValues(6);
        String title = sourceFile.getName();
        newValues.put(MediaStore.Video.Media.TITLE,title);
        newValues.put(MediaStore.Video.Media.DISPLAY_NAME,
                sourceFile.getName());
        newValues.put(MediaStore.Video.Media.DATA, sourceFile.getPath());
        newValues.put(MediaStore.Video.Media.DATE_MODIFIED,
                System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Video.Media.SIZE, sourceFile.length());
        newValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(sourceFile.getAbsolutePath());
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        newValues.put(MediaStore.Video.Media.DURATION,time);
        Uri uri = getContext().getContentResolver().insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, newValues);
        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        localIntent.setData(uri);
        getContext().sendBroadcast(localIntent);
    }

    private Bitmap centerCrop(Bitmap bm, int newWidth, int newHeight){
        int w = bm.getWidth(); // 得到图片的宽，高
        int h = bm.getHeight();
        int retX;
        int retY;
        double wh = (double) w / (double) h;
        double nwh = (double) newWidth / (double) newHeight;
        if (wh > nwh) {
            retX = h * newWidth / newHeight;
            retY = h;
        } else {
            retX = w;
            retY = w * newHeight / newWidth;
        }
        int startX = w > retX ? (w - retX) / 2 : 0;//基于原图，取正方形左上角x坐标
        int startY = h > retY ? (h - retY) / 2 : 0;
        Bitmap bitmap = Bitmap.createBitmap(bm, startX, startY, retX, retY, null, false);
        return Bitmap.createScaledBitmap(bitmap,newWidth,newHeight,false);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility==GONE){
            textLayout.showCover(null);
        }
    }

    public void release() {
        if (waterMark!=null&&!waterMark.isRecycled()){
            waterMark.recycle();
        }
    }

    public boolean back(){
        if (friendView!=null&&friendView.getVisibility()==VISIBLE){
            friendView.gone();
            return true;
        }
        return false;
    }
}
