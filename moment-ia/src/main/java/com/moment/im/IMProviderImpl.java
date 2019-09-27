package com.moment.im;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

@Route(path = "/im/provider/data")
public class IMProviderImpl implements IMProvider {

    @Override
    public void init(Context context) {

    }

    @Override
    public Observable<String> getUserContacts() {
        return Observable.fromCallable(() -> {

            List<User> recent = new ArrayList<>();
            for (int i=0;i<3;i++){
                User user = new User();
                user.id = "recent"+i;
                user.name = "recent"+".name."+i;
                user.avatar = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570091507&di=3ae96041e99144700b4aff6e2cf7b90d&imgtype=jpg&er=1&src=http%3A%2F%2Fwww.uimaker.com%2Fuploads%2Fallimg%2F120305%2F1-1203050956310-L.jpg";
                recent.add(user);
            }
            List<User> relation = new ArrayList<>();
            for (int i=0;i<5;i++){
                User user = new User();
                user.id = "relation"+i;
                user.name = "relation"+".name."+i;
                user.avatar = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570091507&di=3ae96041e99144700b4aff6e2cf7b90d&imgtype=jpg&er=1&src=http%3A%2F%2Fwww.uimaker.com%2Fuploads%2Fallimg%2F120305%2F1-1203050956310-L.jpg";
                relation.add(user);
            }
            Map<String, List<User>> userList = new HashMap<>();
            userList.put("recent", recent);
            userList.put("relation", relation);
            Gson gson = new Gson();
            return gson.toJson(userList);
        });
    }

    @Override
    public void sendMoment(String json) {
        Log.e("IMProviderImpl","json:"+json);
    }
}
