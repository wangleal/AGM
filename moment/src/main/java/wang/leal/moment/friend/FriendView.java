package wang.leal.moment.friend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comment.im.MediatorIM;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import wang.leal.moment.R;

public class FriendView extends RelativeLayout {
    private FriendAdapter friendAdapter;
    private Disposable disposable;
    private TextView tvSendNames;
    private ImageView ivPhotoPreview;
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
            List<Friend> friendList = friendAdapter.getCheckedFriends();
            for (Friend friend:friendList){
                MediatorIM.getIMProvider().sendMoment(new Gson().toJson(friend));
            }
            gone();
        });
        tvSendNames = findViewById(R.id.tv_send_nicknames);
        ScrollingMovementMethod movementMethod = (ScrollingMovementMethod) ScrollingMovementMethod.getInstance();
        tvSendNames.setMovementMethod(movementMethod);
        tvSendNames.setHorizontallyScrolling(true);
        ivPhotoPreview = findViewById(R.id.iv_photo_preview);
    }

    public void show(Bitmap bitmap){
        setVisibility(VISIBLE);
        if (friendAdapter!=null){
            getFriends();
        }
        ivPhotoPreview.setImageBitmap(bitmap);
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
}
