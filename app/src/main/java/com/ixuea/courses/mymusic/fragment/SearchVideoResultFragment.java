package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.event.OnSearchKeyChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 视频，搜索结果
 * Created by smile on 02/03/2018.
 */

public class SearchVideoResultFragment extends BaseCommonFragment {
    RecyclerView rv;

    public static SearchVideoResultFragment newInstance() {

        Bundle args = new Bundle();
        SearchVideoResultFragment fragment = new SearchVideoResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        EventBus.getDefault().register(this);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchKeyChangedEvent(OnSearchKeyChangedEvent event) {
        fetchData(event.getContent());
    }

    private void fetchData(String content) {
        //TODO 视频搜索
    }

    @Override
    protected void initDatas() {
        super.initDatas();

    }

    @Override
    protected void initListener() {
        super.initListener();
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend, null);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
