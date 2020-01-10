package com.ixuea.courses.mymusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.ImageActivity;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.event.OnRecordClickEvent;
import com.ixuea.courses.mymusic.domain.event.OnStartRecordEvent;
import com.ixuea.courses.mymusic.domain.event.OnStopRecordEvent;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.view.RecordView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MusicRecordFragment extends BaseCommonFragment implements View.OnClickListener, View.OnLongClickListener {

    private RecordView rv;
    private Song data;

    public static MusicRecordFragment newInstance(Song data) {

        Bundle args = new Bundle();
        args.putSerializable(Consts.DATA,data);

        MusicRecordFragment fragment = new MusicRecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv=findViewById(R.id.rv);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        data = (Song) getArguments().getSerializable(Consts.DATA);
        rv.setAlbumUri(data.getBanner());
    }

    @Override
    protected void initListener() {
        super.initListener();

        rv.setOnClickListener(this);
        rv.setOnLongClickListener(this);
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_music,container,false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRecordEvent(OnStartRecordEvent event) {
        //由于Fragment放入ViewPager总
        //他的生命周期就改变了
        //所以不能通过onResume这样的方法判断，当前Fragment是否显示
        //所有这里解决方法是，通过事件传递当前音乐
        //如果当前音乐匹配当前Fragment就是操作当前fragment
        //如果不是就忽略
        if (event.getSong()==data) {
            rv.startAlbumRotate();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopRecordEvent(OnStopRecordEvent event) {
        if (event.getSong()==data) {
            rv.stopAlbumRotate();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new OnRecordClickEvent());
    }

    @Override
    public boolean onLongClick(View v) {
        albumPreview();
        return true;
    }

    private void albumPreview() {
        Intent intent = new Intent(getActivity(), ImageActivity.class);
        intent.putExtra(Consts.ID, data.getId());
        intent.putExtra(Consts.STRING, data.getBanner());
        startActivity(intent);

        //makeSceneTransitionAnimation方法的第一个参数：activity
        //第二个：共享元素的控件
        //第三个：共享元素的名称，就是android:transitionName设置的值
        //api 21才有，当前可以找兼容库来实现
        //startActivity(intent,
        //        ActivityOptions.makeSceneTransitionAnimation(this, rv, "banner").toBundle());

    }
}
