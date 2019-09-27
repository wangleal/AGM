package wang.leal.moment.friend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moment.im.MediatorIM;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.liuliu.http.HttpException;
import io.liuliu.http.NetObserver;
import io.liuliu.http.RetrofitFactory;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import wang.leal.moment.R;
import wang.leal.moment.http.FileUploader;
import wang.leal.moment.http.UploadListener;

public class FriendView extends RelativeLayout {
    private FriendAdapter friendAdapter;
    private Disposable disposable;
    private TextView tvSendNames;
    private ImageView ivPhotoPreview;
    private boolean isSend = false;
    public FriendView(Context context) {
        super(context);
        initView();
    }

    public FriendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FriendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        setBackgroundColor(Color.parseColor("#C0000000"));
        LayoutInflater.from(getContext()).inflate(R.layout.view_moment_friend,this,true);
        RecyclerView recyclerView = findViewById(R.id.rv_friend);
        ImageView ivBack = findViewById(R.id.iv_friend_back);
        ivBack.setOnClickListener(v -> gone());
        friendAdapter = new FriendAdapter();
        friendAdapter.setListener(() -> {
            List<Friend> friendList = friendAdapter.getCheckedFriends();
            StringBuilder stringBuilder = new StringBuilder();
            for (Friend friend:friendList){
                if (stringBuilder.length()>0){
                    stringBuilder.append(",");
                }
                stringBuilder.append(friend.name);
            }
            String text = stringBuilder.toString();
            tvSendNames.setText(text);
            int width = tvSendNames.getWidth();
            Rect rect = new Rect();
            tvSendNames.getPaint().getTextBounds(text, 0,text.length(),rect);
            if (rect.right>width){
                tvSendNames.scrollTo(rect.right-width,0);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.shape_camera_friend_divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(friendAdapter);
        findViewById(R.id.iv_friend_send).setOnClickListener(v -> {
            isSend = true;
            sendToFriend();
            gone();
        });
        tvSendNames = findViewById(R.id.tv_send_nicknames);
        ScrollingMovementMethod movementMethod = (ScrollingMovementMethod) ScrollingMovementMethod.getInstance();
        tvSendNames.setMovementMethod(movementMethod);
        tvSendNames.setHorizontallyScrolling(true);
        ivPhotoPreview = findViewById(R.id.iv_photo_preview);
    }

    private String mosaicPath;
    public void show(Bitmap bitmap){
        isSend = false;
        filePath = null;
        fileType = null;
        mosaicPath = null;
        saveToFile(bitmap);
        setVisibility(VISIBLE);
        if (friendAdapter!=null){
            getFriends();
        }
        ivPhotoPreview.setImageBitmap(bitmap);
    }

    private void saveToFile(Bitmap bitmap){
        if (bitmap==null){
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    File photoFile = getMosaicFile();
                    if (!photoFile.exists()) {
                        photoFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    mosaicPath = photoFile.getAbsolutePath();
                    sendToFriend();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private File getMosaicFile() {
        File rootFileDir = getContext().getExternalCacheDir();
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

    public void gone(){
        setVisibility(GONE);
        if (disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    private void getFriends(){
        Observable<String> observable = MediatorIM.getIMProvider().getUserContacts();
        disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Friends friends = new Gson().fromJson(s,Friends.class);
                    friendAdapter.setFriends(friends);
                    friendAdapter.notifyDataSetChanged();
                });
    }

    private String filePath;
    private String fileType;
    public void setFilePath(String filePath,String fileType){
        this.filePath = filePath;
        this.fileType = fileType;
        Log.e("FriendView","set file path:"+filePath);
        sendToFriend();
    }

    public void sendToFriend(){
        Log.e("FriendView","isSend:"+isSend+",filePath:"+filePath+",fileType:"+fileType);
        if (isSend&& !TextUtils.isEmpty(filePath)&&!TextUtils.isEmpty(mosaicPath)){
            uploadFile();
        }
    }

    private void uploadFile(){
//        FileUploader.upload("http://10.110.16.193:6767/v1/moment", filePath,fileType,mosaicPath, new UploadListener() {
        FileUploader.upload("https://beta-static-upload.huiqu6.com/v1/moment", filePath,fileType,mosaicPath, new UploadListener() {
            @Override
            public void onProgress(long progress, long total) {
                Log.e("FriendView","progress:"+progress+",total:"+total);
            }

            @Override
            public void onComplete(String response) {
                Log.e("FriendView","response:"+response);
                String openSourceId = null;
                String lockSourceId = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    int code = jsonObject.getInt("error_code");
                    if (code==0){
                        openSourceId = dataObject.getString("open_resource_id");
                        lockSourceId = dataObject.getString("lock_resource_id");

                        createMoment(openSourceId,lockSourceId,fileType);
                    }else {
                        //failed
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isSend = false;
            }

            @Override
            public void onFailed(Throwable throwable) {
                Log.e("FriendView","error:"+throwable.getMessage());
            }
        });
    }

    private void createMoment(String openSourceId,String lockSourceId,String fileType){
        if (TextUtils.isEmpty(filePath)){
            return;
        }
        String time  ="0";
        if (fileType.equals("video/mp4")){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
            // 取得视频的长度(单位为毫秒)
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        }
        RetrofitFactory.getRetrofit("https://beta-moment.huiqu6.com").create(MomentApi.class)
                .createMoment(lockSourceId,openSourceId,"",fileType,"image/mp4",Long.parseLong(time))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetObserver<Object>() {
                    @Override
                    public void onError(HttpException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),"创建Moment失败",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object object) {
                        Gson gson = new Gson();
                        if (object!=null){
                            try {
                                String json = gson.toJson(object);
                                JSONObject jsonObject = new JSONObject(json);
                                int code = jsonObject.getInt("error_code");
                                if (code==0){
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    String data = dataObject.toString();
                                    List<Friend> friendList = friendAdapter.getCheckedFriends();
                                    for (Friend friend:friendList){
                                        friend.openResourceId = openSourceId;
                                        friend.lockResourceId = lockSourceId;
                                        MediatorIM.getIMProvider().sendMoment(data);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
}
