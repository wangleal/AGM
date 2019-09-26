package com.comment.im;

import com.alibaba.android.arouter.facade.template.IProvider;

import io.reactivex.Observable;

public interface IMProvider extends IProvider {

    /**
     * 获取联系人 (最近联系人，好友关系)
     *
     * @return Json
     */
    Observable<String> getUserContacts();

    /**
     * 发送moment
     *
     * @param json
     */
    void sendMoment(String json);
}
