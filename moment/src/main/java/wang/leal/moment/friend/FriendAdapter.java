package wang.leal.moment.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import wang.leal.moment.R;

public class FriendAdapter extends RecyclerView.Adapter {

    private static final int TYPE_GROUP = 1;
    private static final int TYPE_FRIEND = 2;
    private Friends friends;
    void setFriends(Friends friends){
        this.friends = friends;
    }

    List<Friend> getCheckedFriends(){
        List<Friend> friendList = new ArrayList<>();
        if (friends==null){
            return friendList;
        }
        if (friends.recent!=null){
            for (Friend friend:friends.recent){
                if (friend.isChecked){
                    friendList.add(friend);
                }
            }
        }
        if (friends.relation!=null){
            for (Friend friend:friends.relation){
                if (friend.isChecked){
                    friendList.add(friend);
                }
            }
        }
        return friendList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==TYPE_GROUP){
            return new GroupHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment_group,parent,false));
        }else{
            return new FriendHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment_friend,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupHolder){
            GroupHolder groupHolder = (GroupHolder) holder;
            if (position==0){
                groupHolder.showGroup("最近联系的好友");
            }else {
                groupHolder.showGroup("好友");
            }
        }else {
            FriendHolder friendHolder = (FriendHolder) holder;
            Friend friend;
            int recentSize = friends.recent.size();
            if (position<recentSize+1){
                friend = friends.recent.get(position-1);
            }else {
                friend = friends.relation.get(position-recentSize-2);
            }
            friendHolder.showFriend(friend);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int recentSize = friends.recent.size();
        if (position==0||position==recentSize+1){
            return TYPE_GROUP;
        }else {
            return TYPE_FRIEND;
        }
    }

    @Override
    public int getItemCount() {
        if (friends==null||((friends.recent==null||friends.recent.size()==0)&&(friends.relation==null||friends.relation.size()==0))){
            return 0;
        }else {
            if (friends.recent==null||friends.recent.size()==0){
                return friends.relation.size()+1;
            }else if (friends.relation==null||friends.relation.size()==0){
                return friends.recent.size()+1;
            }else {
                return friends.relation.size()+1+friends.recent.size()+1;
            }
        }
    }

    static class GroupHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName;
        GroupHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
        }

        void showGroup(String text){
            tvGroupName.setText(text);
        }

    }

    class FriendHolder extends RecyclerView.ViewHolder{
        private ImageView ivAvatar;
        private TextView tvName;
        private CheckBox cbCheck;
        FriendHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_friend_avatar);
            tvName = itemView.findViewById(R.id.tvf_friend_name);
            cbCheck = itemView.findViewById(R.id.cb_friend_check);
        }

        void showFriend(Friend friend){
            loadRound(itemView.getContext(),friend.avatar,ivAvatar);
            tvName.setText(friend.name);
            cbCheck.setChecked(friend.isChecked);
            cbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                friend.isChecked = isChecked;
                if (listener!=null){
                    listener.onCheckedChange();
                }
            });
        }
    }

    private static void loadRound(Context context, String url, ImageView iv) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_camera_circle_default)
                .transforms(new CenterCrop(), new CircleCrop());
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);
    }

    private Listener listener;
    void setListener(Listener listener){
        this.listener = listener;
    }

    interface Listener{
        void onCheckedChange();
    }
}
