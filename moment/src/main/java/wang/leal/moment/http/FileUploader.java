package wang.leal.moment.http;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class FileUploader {

    public static void upload(String url, String filePath,String fileType,String mosaicPath,UploadListener uploadListener){
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        File file = new File(filePath);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(fileType), file);
        builder.addFormDataPart("open_file", file.getName(), new FileRequestBody(requestFile, uploadListener));
        File mosaicFile = new File(mosaicPath);
        RequestBody requestMosaic = RequestBody.create(MediaType.parse("image/jpeg"),mosaicFile);
        builder.addFormDataPart("lock_file", file.getName(), requestMosaic);
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (uploadListener!=null){
                    uploadListener.onFailed(e);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (uploadListener!=null){
                    uploadListener.onComplete(response.body().string());
                }
            }
        });
    }

    public static class FileRequestBody extends RequestBody {
        private RequestBody requestBody;
        private long mContentLength;
        private UploadListener uploadListener;
        public FileRequestBody(RequestBody requestBody, UploadListener uploadListener) {
            this.requestBody = requestBody;
            this.uploadListener = uploadListener;
        }

        //文件的总长度
        @Override
        public long contentLength() {
            try {
                if (mContentLength == 0)
                    mContentLength = requestBody.contentLength();
                return mContentLength;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            ByteSink byteSink = new ByteSink(sink);
            BufferedSink mBufferedSink = Okio.buffer(byteSink);
            requestBody.writeTo(mBufferedSink);
            mBufferedSink.flush();
        }


        private final class ByteSink extends ForwardingSink {
            //已经上传的长度
            private long mByteLength = 0L;

            ByteSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                mByteLength += byteCount;
                uploadListener.onProgress(mByteLength, contentLength());
            }
        }
    }
}
