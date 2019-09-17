package wang.leal.moment.recorder;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MP4Muxer implements AudioEncoder.Callback,VideoEncoder.Callback {
    private final String TAG = "MP4Muxer";
    private MediaMuxer mediaMuxer;
    private int videoIndex = -1;
    private int audioIndex = -1;
    private AudioFormat audioFormat;
    private VideoFormat videoFormat;
    private String filePath;
    private Context context;

    public MP4Muxer(Context context){
        this.context = context.getApplicationContext();
    }

    public void start(){
        start(null);
    }

    public synchronized void start(String filePath){
        if (mediaMuxer ==null){
            try {
                if (TextUtils.isEmpty(filePath)){
                    filePath = getDefaultPath();
                }
                this.filePath = filePath;
                mediaMuxer = new MediaMuxer(filePath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                startMuxer();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private long duration = 0;
    private synchronized void startMuxer(){
        if (audioFormat!=null&&videoFormat!=null&&mediaMuxer!=null){
            audioIndex = mediaMuxer.addTrack(audioFormat.format);
            videoIndex = mediaMuxer.addTrack(videoFormat.format);
            mediaMuxer.start();
            duration = System.currentTimeMillis();
        }
    }

    @Override
    public void onVideoConfig(ByteBuffer encodeData, BufferInfo bufferInfo) {
        Log.e(TAG,"onVideoConfig presentationTimeUs:"+bufferInfo.bufferInfo.presentationTimeUs);
        writeSample(videoIndex,bufferInfo, encodeData);
    }

    @Override
    public void onVideoKeyFrame(ByteBuffer encodeData, BufferInfo bufferInfo) {
        Log.e(TAG,"onVideoKeyFrame presentationTimeUs:"+bufferInfo.bufferInfo.presentationTimeUs);
        writeSample(videoIndex,bufferInfo, encodeData);
    }

    @Override
    public void onVideoPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo) {
        Log.e(TAG,"onVideoPartialFrame presentationTimeUs:"+bufferInfo.bufferInfo.presentationTimeUs);
        writeSample(videoIndex,bufferInfo, encodeData);
    }

    @Override
    public void onVideoEnd(ByteBuffer encodeData, BufferInfo bufferInfo) {
        Log.e(TAG,"onVideoEnd presentationTimeUs:"+bufferInfo.bufferInfo.presentationTimeUs);
        writeSample(videoIndex,bufferInfo, encodeData);
        stop();
    }

    @Override
    public void onVideoFormatChange(VideoFormat videoFormat) {
        Log.e(TAG,"onVideoFormatChange");
        this.videoFormat = videoFormat;
        startMuxer();
    }

    @Override
    public void onAudioConfig(ByteBuffer encodeData, BufferInfo bufferInfo) {
        Log.e(TAG,"onAudioConfig");
        writeSample(audioIndex,bufferInfo, encodeData);
    }

    @Override
    public void onAudioPartialFrame(ByteBuffer encodeData, BufferInfo bufferInfo) {
        Log.e(TAG,"onAudioPartialFrame");
        writeSample(audioIndex,bufferInfo, encodeData);
    }

    @Override
    public void onAudioFormatChange(AudioFormat audioFormat) {
        Log.e(TAG,"onAudioFormatChange");
        this.audioFormat = audioFormat;
        startMuxer();
    }

    private boolean isMuxerStart(){
        return videoIndex!=-1&&audioIndex!=-1;
    }

    private synchronized void writeSample(int index,BufferInfo bufferInfo, ByteBuffer encodeData) {
        try {
            if (mediaMuxer!=null&&isMuxerStart()){
                mediaMuxer.writeSampleData(index,encodeData.duplicate(),bufferInfo.bufferInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void signalEndOfStream() {
        MediaCodec.BufferInfo eos = new MediaCodec.BufferInfo();
        ByteBuffer buffer = ByteBuffer.allocate(0);
        eos.set(0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
        if (videoIndex != -1) {
            writeSample(videoIndex,BufferInfo.createByBufferInfo(eos),buffer);
        }
        if (audioIndex != -1) {
            writeSample(audioIndex,BufferInfo.createByBufferInfo(eos),buffer);
        }
        audioIndex = -1;
        videoIndex = -1;
    }

    public synchronized void stop(){
        try {
            if (mediaMuxer !=null){
                signalEndOfStream();
                mediaMuxer.stop();
                mediaMuxer.release();
                this.audioFormat = null;
                this.videoFormat = null;
                mediaMuxer = null;
                insertToMediaStore(new File(filePath));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getDefaultPath() {
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
        return filePath;
    }

    private void insertToMediaStore(File sourceFile){

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
        if (duration!=0){
            duration = System.currentTimeMillis()-duration;
            newValues.put(MediaStore.Video.Media.DURATION,duration);
            duration = 0;
        }
        Uri uri = context.getContentResolver().insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, newValues);
        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        localIntent.setData(uri);
        context.sendBroadcast(localIntent);
        if (this.callback!=null){
            this.callback.onComplete(sourceFile.getAbsolutePath());
        }
    }

    public void release(){
        this.callback = null;
        stop();
    }

    private Callback callback;
    public interface Callback{
        void onComplete(String filePath);
    }
    public void setCallback(Callback callback){
        this.callback = callback;
    }
}
