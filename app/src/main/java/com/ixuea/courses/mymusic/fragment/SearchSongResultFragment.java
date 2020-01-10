package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.MusicPlayerActivity;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.SongAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.event.OnSearchKeyChangedEvent;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.DataUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 歌曲，搜索结果
 * Created by smile on 02/03/2018.
 */

public class SearchSongResultFragment extends BaseCommonFragment implements View.OnClickListener, SongAdapter.OnSongListener {
    RecyclerView rv;
    private SongAdapter adapter;
    private DownloadManager downloadManager;
    private PlayListManager playListManager;

    public static SearchSongResultFragment newInstance() {

        Bundle args = new Bundle();
        SearchSongResultFragment fragment = new SearchSongResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        EventBus.getDefault().register(this);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchKeyChangedEvent(OnSearchKeyChangedEvent event) {
        fetchData(event.getContent());
    }

    private void play(int position) {
        Song data = adapter.getData(position);
        playListManager.setPlayList(adapter.getDatas());
        playListManager.play(data);
        adapter.notifyDataSetChanged();
        startActivity(MusicPlayerActivity.class);
    }


    private void fetchData(String content) {
        Api.getInstance().searchSong(content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<Song>>(getMainActivity()) {
                    @Override
                    public void onSucceeded(final ListResponse<Song> data) {
                        super.onSucceeded(data);
                        adapter.setData(DataUtil.fill(data.getData()));
                    }
                });
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        downloadManager = DownloadService.getDownloadManager(getActivity().getApplicationContext());
        playListManager = MusicPlayerService.getPlayListManager(getActivity().getApplicationContext());

        adapter = new SongAdapter(getActivity(), R.layout.item_song_detail,getChildFragmentManager(), playListManager,downloadManager);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                play(position);
            }
        });
        adapter.setOnSongListener(this);

        rv.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_list, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_day_container:

            break;
        }
    }

    @Override
    public void onCollectionClick(Song song) {

    }

    @Override
    public void onDownloadClick(Song song) {

    }

    @Override
    public void onDeleteClick(Song song) {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
