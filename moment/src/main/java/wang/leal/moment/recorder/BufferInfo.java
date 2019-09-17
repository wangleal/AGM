package wang.leal.moment.recorder;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.os.Build;

public class BufferInfo {
    /**
     * The amount of data (in bytes) in the buffer.  If this is {@code 0},
     * the buffer has no data in it and can be discarded.  The only
     * use of a 0-size buffer is to carry the end-of-stream marker.
     */
    public int size;
    /**
     * The presentation timestamp in microseconds for the buffer.
     * This is derived from the presentation timestamp passed in
     * with the corresponding input buffer.  This should be ignored for
     * a 0-sized buffer.
     */
    public long presentationTimeUs;

    public MediaCodec.BufferInfo bufferInfo;

    private BufferInfo(MediaCodec.BufferInfo bufferInfo){
        this.bufferInfo = bufferInfo;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static BufferInfo createByBufferInfo(MediaCodec.BufferInfo bufferInfo){
        return new BufferInfo(bufferInfo);
    }
}
