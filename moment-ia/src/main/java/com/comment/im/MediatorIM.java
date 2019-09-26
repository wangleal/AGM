package com.comment.im;

import com.alibaba.android.arouter.launcher.ARouter;

public class MediatorIM {

    /**
     * 获取IM数据提供者
     *
     * @return
     */
    public static IMProvider getIMProvider() {
        return (IMProvider) ARouter.getInstance()
                .build("/im/provider/data")
                .navigation();
    }

}
