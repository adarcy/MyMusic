package com.ixuea.courses.mymusic.manager.impl;

import android.content.Context;

import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.reactivex.AbsObserver;

import java.util.HashMap;
import java.util.Map;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by smile on 2018/6/19.
 */

public class UserManagerImpl implements UserManager {
    private static UserManager manager;
    private final Context context;
    private Map<String,User> userCaches=new HashMap<>();

    public UserManagerImpl(Context context) {
        this.context=context.getApplicationContext();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (manager == null) {
            manager = new UserManagerImpl(context);
        }
        return manager;
    }

    @Override
    public void getUser(final String userId, final OnUserListener onUserListener) {
        final User user = userCaches.get(userId);
        if (user!=null) {
            onUserListener.onUser(user);
            return;
        }

        //从网络上获取
        Api.getInstance().userDetail(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsObserver<DetailResponse<User>>() {
                    @Override
                    public void onNext(DetailResponse<User> userDetailResponse) {
                        super.onNext(userDetailResponse);
                        if (userDetailResponse != null && userDetailResponse.getData() != null) {
                            onUserListener.onUser(userDetailResponse.getData());
                            userCaches.put(userId,userDetailResponse.getData());
                        }
                    }
                });
    }
}
