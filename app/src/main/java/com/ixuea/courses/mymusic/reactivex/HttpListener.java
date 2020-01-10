package com.ixuea.courses.mymusic.reactivex;

import android.text.TextUtils;

import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.domain.response.BaseResponse;
import com.ixuea.courses.mymusic.util.ToastUtil;


/**
 * Created by smile on 03/03/2018.
 */

public class HttpListener<T extends BaseResponse> extends AbsObserver<T> {

    private final BaseActivity activity;

    public HttpListener(BaseActivity activity) {
        this.activity=activity;
    }

    public void onSucceeded(T data) {

    }

    public void onFailed(T t, Throwable e) {

        if (activity != null) {
            activity.hideLoading();
        }
        if (e != null) {
            new ExceptHandler(activity).handle(e);
        } else {
            if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                ToastUtil.showSortToast(activity, t.getMessage());
            } else {
                ToastUtil.showSortToast(activity, "未知错误,请稍后再试!");
            }

        }
    }

    public boolean isSuccess(T t) {
        return t.getStatus()==null;
    }


    @Override
    public void onNext(T t) {
        super.onNext(t);
        if (isSuccess(t)) {
            onSucceeded(t);
        } else {
            onFailed(t,null);
        }
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        onFailed(null,e);
    }

}
