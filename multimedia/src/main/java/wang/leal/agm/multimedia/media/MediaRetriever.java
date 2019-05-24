package wang.leal.agm.multimedia.media;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

public class MediaRetriever {
    private MediaMetadataRetriever metadataRetriever;
    public MediaRetriever(String filePath){
        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(filePath);
    }

    public Bitmap getFirstFrame(){
        if (metadataRetriever!=null){
            return metadataRetriever.getFrameAtTime(0);
        }else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    public int getFrameCount(){
        try {
            String count = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            return Integer.parseInt(count);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    public int getStillImageCount(){
        try {
            String count = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_COUNT);
            return Integer.parseInt(count);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int getVideoHeight(){
        try {
            String height = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            return Integer.parseInt(height);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int getVideoWidth(){
        try {
            String width = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            return Integer.parseInt(width);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int getVideoBitrate(){
        try {
            String bitrate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            return Integer.parseInt(bitrate);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void release(){
        if (metadataRetriever!=null){
            metadataRetriever.release();
        }
    }

}
