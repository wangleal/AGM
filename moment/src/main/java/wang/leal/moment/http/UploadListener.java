package wang.leal.moment.http;

public interface UploadListener {

    void onProgress(long progress,long total);
    void onComplete(String response);
    void onFailed(Throwable throwable);

}
