package com.ixuea.courses.mymusic.activity;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.PlayListAdapter;
import com.ixuea.courses.mymusic.domain.Lyric;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.fragment.PlayListDialogFragment;
import com.ixuea.courses.mymusic.listener.OnMusicPlayerListener;
import com.ixuea.courses.mymusic.listener.PlayListListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.parser.LyricsParser;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.view.LyricSingleLineView;

/**
 * Created by smile on 2018/6/23.
 */

public class BaseMusicPlayerActivity extends BaseTitleActivity implements OnMusicPlayerListener, PlayListListener, View.OnClickListener {
    protected LinearLayout ll_play_small_container;
    protected ImageView iv_icon_small_controller;
    protected ImageView iv_play_small_controller;
    protected ImageView iv_play_list_small_controller;
    protected ImageView iv_next_small_controller;
    protected TextView tv_title_small_controller;
    protected LyricSingleLineView tv_info_small_controller;
    protected ProgressBar pb_progress_small;

    protected PlayListManager playListManager;
    protected MusicPlayerManager musicPlayerManager;
    private PlayListDialogFragment playListDialog;

    @Override
    protected void initViews() {
        super.initViews();
        ll_play_small_container = findViewById(R.id.ll_play_small_container);
        iv_icon_small_controller = findViewById(R.id.iv_icon_small_controller);
        tv_title_small_controller = findViewById(R.id.tv_title_small_controller);
        tv_info_small_controller = findViewById(R.id.tv_info_small_controller);
        iv_play_small_controller = findViewById(R.id.iv_play_small_controller);
        iv_play_list_small_controller = findViewById(R.id.iv_play_list_small_controller);
        iv_next_small_controller = findViewById(R.id.iv_next_small_controller);
        pb_progress_small = findViewById(R.id.pb_progress_small);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        playListManager = MusicPlayerService.getPlayListManager(getApplicationContext());
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //写到这里是因为，可以在播放界面将播放列表清空了，那这里就需要隐藏
        if (playListManager.getPlayList() != null&&playListManager.getPlayList().size()>0) {
            ll_play_small_container.setVisibility(View.VISIBLE);
            Song song = playListManager.getPlayData();
            if (song != null) {
                setFirstData(song);
            }

        } else {
            ll_play_small_container.setVisibility(View.GONE);
        }

        //注册监听器
        musicPlayerManager.addOnMusicPlayerListener(this);
        playListManager.addPlayListListener(this);
    }

    public void setFirstData(Song data) {
        pb_progress_small.setMax((int) data.getDuration());
        pb_progress_small.setProgress(sp.getLastSongProgress());
        ImageUtil.show(getActivity(), iv_icon_small_controller, data.getBanner());
        tv_title_small_controller.setText(data.getTitle());
    }


    @Override
    protected void onStop() {
        super.onStop();
        musicPlayerManager.removeOnMusicPlayerListener(this);
        playListManager.removePlayListListener(this);
    }

    @Override
    protected void initListener() {
        super.initListener();

        ll_play_small_container.setOnClickListener(this);
        iv_play_small_controller.setOnClickListener(this);
        iv_play_list_small_controller.setOnClickListener(this);
        iv_next_small_controller.setOnClickListener(this);

    }

    @Override
    public void onProgress(long progress, long total) {
        pb_progress_small.setProgress((int) progress);
        tv_info_small_controller.show(progress);
    }

    @Override
    public void onPaused(Song data) {
        iv_play_small_controller.setSelected(false);
    }

    @Override
    public void onPlaying(Song data) {
        //这种图片切换可以使用Selector来实现
        iv_play_small_controller.setSelected(true);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer, Song data) {
        setFirstData(data);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onError(MediaPlayer mp, int what, int extra) {

    }

    @Override
    public void onDataReady(Song song) {
        setLyric(song.getLyric());
    }

    private void setLyric(Lyric lyric) {
        LyricsParser parser = LyricsParser.parse(lyric.getStyle(), lyric.getContent());
        parser.parse();
        if (parser.getLyric() != null) {
            tv_info_small_controller.setData(parser.getLyric());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_small_container:
                startActivity(MusicPlayerActivity.class);
                break;
            case R.id.iv_play_small_controller:
                playOrPause();
                break;
            case R.id.iv_play_list_small_controller:
                showPlayListDialog();
                break;
            case R.id.iv_next_small_controller:
                Song songNext = playListManager.next();
                playListManager.play(songNext);
                break;
        }
    }

    private void playOrPause() {
        if (musicPlayerManager.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    private void play() {
        playListManager.resume();
    }

    private void pause() {
        playListManager.pause();
    }

    private void showPlayListDialog() {
        playListDialog = new PlayListDialogFragment();
        playListDialog.setCurrentSong(playListManager.getPlayData());
        playListDialog.setData(playListManager.getPlayList());
        playListDialog.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                playListDialog.dismiss();
                playListManager.play(playListManager.getPlayList().get(position));
                playListDialog.setCurrentSong(playListManager.getPlayData());
                playListDialog.notifyDataSetChanged();
            }
        });
        playListDialog.setOnRemoveClickListener(new PlayListAdapter.OnRemoveClickListener() {
            @Override
            public void onRemoveClick(int position) {
                Song currentSong = playListManager.getPlayList().get(position);
                playListManager.delete(currentSong);
                playListDialog.removeData(position);
                currentSong = playListManager.getPlayData();
                if (currentSong == null) {
                    playListManager.destroy();
                    finish();
                } else {
                    playListDialog.setCurrentSong(currentSong);
                }
            }
        });

        playListDialog.show(getSupportFragmentManager(), "dialog");
    }
}
