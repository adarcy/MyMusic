package com.ixuea.courses.mymusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.util.Consts;


/**
 * Created by smile on 02/03/2018.
 */

public abstract class BaseFragment extends Fragment {
    /**
     * 找控件
     */
    protected void initViews() {

    }

    /**
     * 动态设置样式，颜色，宽高，背景
     */
    protected void initStyles() {
    }

    /**
     *设置数据
     */
    protected void initDatas() {
    }

    /**
     * 绑定监听器
     */
    protected void initListener() {
    }


    @Override
    public void onResume() {

        super.onResume();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutView(inflater,container,savedInstanceState);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initViews();
        initStyles();
        initDatas();
        initListener();
        super.onViewCreated(view, savedInstanceState);
    }

    /**返回要显示的View
     * @return
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    protected abstract View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Nullable
    public final <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        ButterKnife(this);
    }

    protected void startActivityExtraId(Class<?> clazz, String id) {
        Intent intent = new Intent(getActivity(), clazz);
        if (!TextUtils.isEmpty(id)) {
            intent.putExtra(Consts.ID,id);
        }
        startActivity(intent);
    }

    protected void startActivityAfterFinishThis(Class<?> clazz) {
        startActivity(new Intent(getActivity(),clazz));
        getActivity().finish();
    }

    protected void startActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    public BaseActivity getMainActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisibleToUser();
        } else {
            onInvisibleToUser();
        }
    }

    protected void onInvisibleToUser() {

    }

    protected void onVisibleToUser() {

    }
}
