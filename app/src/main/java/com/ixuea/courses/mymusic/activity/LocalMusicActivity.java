package com.ixuea.courses.mymusic.activity;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.SongAdapter;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.event.ScanMusicCompleteEvent;
import com.ixuea.courses.mymusic.fragment.SortDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;

public class LocalMusicActivity extends BaseMusicPlayerActivity {

    private static final String TAG = "TAG";

    private RecyclerView rv;
    private View ll_play_all_container;
    private TextView tv_count;

    private java.util.List<Song> songs;

    private SongAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private DownloadManager downloadManager;

    //private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
    }
    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        downloadManager = DownloadService.getDownloadManager(getApplicationContext());

        EventBus.getDefault().register(this);

        adapter = new SongAdapter(getActivity(), R.layout.item_song_detail,getSupportFragmentManager(), playListManager,downloadManager);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                play(position);
            }
        });

        adapterWrapper = new LRecyclerViewAdapter(adapter);

        adapterWrapper.addHeaderView(createHeaderView());


        rv.setAdapter(adapterWrapper);

        fetchData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ll_play_all_container.setOnClickListener(this);
    }

    private void fetchData() {
        java.util.List<Song> songs = getData();
        if (songs != null && songs.size() > 0) {
            setData(songs);
        } else {
            toScanLocalMusic();

        }
    }

    private void toScanLocalMusic() {
        //去扫描本地歌曲
        startActivity(ScanLocalMusicActivity.class);
    }

    private void setData(java.util.List<Song> songs) {
        this.songs=songs;
        adapter.setData(songs);
        tv_count.setText(getResources().getString(R.string.music_count, songs.size()));
    }

    private java.util.List<Song> getData() {
        return orm.queryLocalMusic(sp.getUserId(),Song.SORT_KEYS[sp.getLocalMusicSortKey()]);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanMusicCompleteEvent(ScanMusicCompleteEvent event) {
        java.util.List<Song> songs = getData();
        if (songs != null && songs.size() > 0) {
            setData(songs);
        }
    }

    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_local_song, (ViewGroup) rv.getParent(), false);
        ll_play_all_container = top.findViewById(R.id.ll_play_all_container);
        tv_count = top.findViewById(R.id.tv_count);

        return top;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_all_container:
                play(0);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void play(int position) {
        if (adapter.getDatas().size() > 0) {
            Song data = adapter.getData(position);
            playListManager.setPlayList(adapter.getDatas());
            playListManager.play(data);
            adapter.notifyDataSetChanged();
            startActivity(MusicPlayerActivity.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.local_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_scan_local_music:
                toScanLocalMusic();
                break;
            case R.id.action_select_sort:
                showSortDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        SortDialogFragment sortDialogFragment = new SortDialogFragment();
        sortDialogFragment.show(getSupportFragmentManager(),sp.getLocalMusicSortKey(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: "+which);
                dialog.dismiss();
                sp.setLocalMusicSortKey(which);
                fetchData();
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
