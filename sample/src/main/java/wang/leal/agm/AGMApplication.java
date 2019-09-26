package wang.leal.agm;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

public class AGMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
    }
}
