package com.ixuea.courses.mymusic.manager.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.listener.OnFloatingListener;
import com.ixuea.courses.mymusic.listener.OnMusicPlayerListener;
import com.ixuea.courses.mymusic.listener.PlayListListener;
import com.ixuea.courses.mymusic.manager.FloatingLayoutManager;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.reactivex.AbsObserver;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.DataUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.NotificationUtil;
import com.ixuea.courses.mymusic.util.OrmUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;
import com.ixuea.courses.mymusic.util.WidgetUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by smile on 2018/5/30.
 */

public class PlayListManagerImpl implements PlayListManager, OnMusicPlayerListener, OnFloatingListener {
    /**
     * 列表循环
     */
    public static final int MODEL_LOOP_LIST = 0;

    /**
     * 单曲循环
     */
    public static final int MODEL_LOOP_ONE = 1;

    /**
     * 随机循环
     */
    public static final int MODEL_LOOP_RANDOM = 2;
    private static final String TAG = "PlayListManagerImpl";

    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;


    private static final int MSG_DATA_READY = 0;
    private static final long DEFAULT_SAVE_PROGRESS_TIME = 1000;

    private final MusicPlayerManager musicPlayer;
    private final FloatingLayoutManager floatingLayoutManager;
    private final DownloadManager downloadManager;
    private List<Song> datum = new LinkedList<>();

    private static PlayListManager manager;
    private final Context context;
    private Song currentSong;
    private int model = MODEL_LOOP_LIST;

    /**
     * 监听器
     */
    private List<PlayListListener> listeners = new ArrayList<>();
    private final OrmUtil orm;
    private final SharedPreferencesUtil sp;
    private boolean isPlay;
    private long lastTime;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    public Bitmap albumBitmap;
    private BroadcastReceiver notificationMusicReceiver;

    public PlayListManagerImpl(Context context) {
        this.context = context.getApplicationContext();
        musicPlayer = MusicPlayerService.getMusicPlayerManager(context);
        musicPlayer.addOnMusicPlayerListener(this);
        orm = OrmUtil.getInstance(context);
        sp = SharedPreferencesUtil.getInstance(context);

        init();
        initMediaSession();
        initNotificationReceiver();

        floatingLayoutManager=MusicPlayerService.getFloatingLayoutManager(context);
        floatingLayoutManager.setOnFloatingListener(this);
        downloadManager = DownloadService.getDownloadManager(context);
    }


    private void initNotificationReceiver() {
        notificationMusicReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Consts.ACTION_LIKE.equals(intent.getAction())) {

                }else if (Consts.ACTION_PREVIOUS.equals(intent.getAction())) {
                    play(previous());
                }else if (Consts.ACTION_PLAY.equals(intent.getAction())) {
                    playOrPause();
                }else if (Consts.ACTION_NEXT.equals(intent.getAction())) {
                    play(next());
                }else if (Consts.ACTION_LYRIC.equals(intent.getAction())) {
                    showOrHideGlobalLyric();
                }
            }
        };


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.ACTION_LIKE);
        intentFilter.addAction(Consts.ACTION_PREVIOUS);
        intentFilter.addAction(Consts.ACTION_PLAY);
        intentFilter.addAction(Consts.ACTION_NEXT);
        intentFilter.addAction(Consts.ACTION_LYRIC);
        context.registerReceiver(notificationMusicReceiver,intentFilter);
    }

    private void playOrPause() {
        if (musicPlayer.isPlaying()) {
            pause();
        } else {
            resume();
        }
    }

    /**
     * 显示或者隐藏桌面歌词
     */
    private void showOrHideGlobalLyric() {
        if (floatingLayoutManager.isShowing()) {
            floatingLayoutManager.hide();
        } else {
            floatingLayoutManager.show();
        }
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(context, TAG);

        //设置哪些事件回调我们
        //FLAG_HANDLES_MEDIA_BUTTONS：媒体控制按钮
        //FLAG_HANDLES_TRANSPORT_CONTROLS：传输控制命令，耳机，蓝牙等控制
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //设置哪些按钮可用
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS);
        mediaSession.setPlaybackState(stateBuilder.build());

        //设置回调
        mediaSession.setCallback(new MediaSessionCallback());

        //激活控制器
        mediaSession.setActive(true);
    }

    private void init() {
        //初始化的时候从数据库中获取播放列表
        List<Song> songs = orm.queryPlayList(sp.getUserId());
        this.datum.clear();
        this.datum.addAll(songs);

        if (datum.size() > 0) {
            //获取当前播放的歌曲
            String id = sp.getLastPlaySongId();
            if (StringUtils.isNotBlank(id)) {
                //在播放列表中查看该歌曲
                Song song = null;
                for (Song s : datum) {
                    if (s.getId().endsWith(id)) {
                        song = s;
                        break;
                    }
                }

                if (song == null) {
                    //表示没找到，可能各种原因
                    defaultPlaySong();
                } else {
                    currentSong = song;
                }
            } else {
                defaultPlaySong();
            }
        }


    }

    private void defaultPlaySong() {
        currentSong = datum.get(0);
    }

    public static synchronized PlayListManager getInstance(Context context) {
        if (manager == null) {
            manager = new PlayListManagerImpl(context);
        }
        return manager;
    }

    @Override
    public void addPlayListListener(PlayListListener listener) {
        listeners.add(listener);
        //发布一次当前的状态，目的是让新添加的监听器能够获取上一次的状态
        if (currentSong != null && currentSong.getLyric() != null) {
            sendDataReadyMessage();
        }
    }

    private void sendDataReadyMessage() {
        handler.obtainMessage(MSG_DATA_READY).sendToTarget();
    }

    @Override
    public void removePlayListListener(PlayListListener listener) {
        listeners.remove(listener);
    }


    @Override
    public List<Song> getPlayList() {
        return datum;
    }

    @Override
    public void setPlayList(List<Song> datum) {
        //将原来数据在PlayList的标志去掉，并保持到数据库
        DataUtil.changePlayListFlag(this.datum,false);
        saveAll(this.datum);
        this.datum.clear();
        //将当前传递进来的数据更改为在PlayList标志，并添加到集合
        this.datum.addAll(DataUtil.changePlayListFlag(datum,true));
        //在这里要保存数据
        saveAll(this.datum);
    }

    private void saveAll(List<Song> datum) {
        //然后在保存当前所有数据
        for (Song song : datum) {
            orm.saveSong(song, sp.getUserId());
        }
    }

    @Override
    public void play(Song song) {
        isPlay = true;
        this.currentSong = song;
        if (song.isLocal()) {
            //本地音乐，就不要拼接地址了
            musicPlayer.play(currentSong.getUri(), song);
        } else {
            //判断是否有下载的对象
            DownloadInfo downloadInfo = downloadManager.getDownloadById(song.getId());
            if (downloadInfo != null && downloadInfo.getStatus()==DownloadInfo.STATUS_COMPLETED) {
                //播放下载的音乐
                musicPlayer.play(downloadInfo.getPath(), song);
            } else {
                musicPlayer.play(ImageUtil.getImageURI(currentSong.getUri()), song);
            }

            //musicPlayer.play(ImageUtil.getImageURI(currentSong.getUri()), song);

        }
        sp.setLastPlaySongId(currentSong.getId());
    }

    @Override
    public void pause() {
        musicPlayer.pause();
    }

    @Override
    public void resume() {
        if (isPlay) {
            musicPlayer.resume();
        } else {
            //到这里，是应用开启后，第一次点继续播放
            //而这时内部其实还没有准备播放，所以应该调用播放
            play(currentSong);
            if (sp.getLastSongProgress() > 0) {
                musicPlayer.seekTo(sp.getLastSongProgress());
            }
        }
    }

    @Override
    public void delete(Song song) {
        if (song.equals(currentSong)) {
            //如果删除的是当前播放歌曲，应该停止当前播放，并播放下一首音乐
            pause();

            Song next = next();
            //只有获取的下一个条目不是自己才进行播放
            //因为自己要删除
            if (next.equals(currentSong)) {
                //没有歌曲可以播放了
                currentSong = null;
            } else {
                play(next);
            }

            datum.remove(song);
        } else {
            //如果删除的不是正在播放的，就直接删除
            datum.remove(song);
        }

        orm.deleteSong(song);
    }

    @Override
    public Song getPlayData() {
        return currentSong;
    }

    @Override
    public Song next() {
        if (datum.size() == 0) {
            return null;
        }
        switch (model) {
            case MODEL_LOOP_RANDOM:
                //在0~datum.size()中，不包含datum.size()
                int i = new Random().nextInt(datum.size());
                Song song = datum.get(i);
                return song;
            default:
                int index = datum.indexOf(currentSong);
                if (index != -1) {
                    //找到了
                    //如果当前播放是列表最后一个，那就从0开始播放
                    if (index == datum.size() - 1) {
                        index = 0;
                    } else {
                        index++;
                    }
                } else {
                    throw new IllegalArgumentException("Can't find current song!");
                }
                return datum.get(index);
        }
    }

    @Override
    public Song previous() {
        if (datum.size() == 0) {
            return null;
        }
        switch (model) {
            case MODEL_LOOP_RANDOM:
                //在0~datum.size()中，不包含datum.size()
                int i = new Random().nextInt(datum.size());
                Song song = datum.get(i);
                return song;
            default:
                //其他模式，例如：列表循环，单曲循环都是这样
                int index = datum.indexOf(currentSong);
                if (index != -1) {
                    //找到了
                    //如果当前播放是列表第一个，那就从最后一个开始播放
                    if (index == 0) {
                        index = datum.size() - 1;
                    } else {
                        index--;
                    }
                } else {
                    throw new IllegalArgumentException("Can't find current song!");
                }
                return datum.get(index);
        }
    }

    @Override
    public int getLoopModel() {
        return model;
    }

    @Override
    public int changeLoopModel() {
        int model = getLoopModel();
        model++;
        if (model > MODEL_LOOP_RANDOM) {
            this.model = 0;
        } else {
            this.model = model;
        }
        if (MODEL_LOOP_ONE == this.model) {
            //循环模式，设置到mediaPlay
            musicPlayer.setLooping(true);
        } else {
            //其他模式关闭循环
            musicPlayer.setLooping(false);
        }
        return this.model;
    }

    @Override
    public void onProgress(long progress, long total) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTime > DEFAULT_SAVE_PROGRESS_TIME) {
            //间隔大于1秒才保存，这样做是免得频繁操作数据库
            sp.setLastSongProgress((int) progress);

            lastTime = currentTimeMillis;
        }
        floatingLayoutManager.onProgress(progress,total);

        WidgetUtil.onProgress(context,progress,total);
    }

    private void updateMediaInfo() {
        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                //歌曲名称
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,currentSong.getTitle())

                //歌手
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist_name())

                //专辑名
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.getTitle())

                //专辑歌手
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, currentSong.getAlbum_title())

                //当前歌曲时长
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentSong.getDuration())

                //当前歌曲的封面
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumBitmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //当前列表总共有多少首音乐
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, getPlayList().size());
        }

        mediaSession.setMetadata(metaData.build());
    }

    @Override
    public void onPaused(Song data) {
        //设置状态，当前播放位置，播放速度
        if (currentSong!=null) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, getPlayList().indexOf(currentSong), 1.0f);
            mediaSession.setPlaybackState(stateBuilder.build());
            NotificationUtil.showMusicNotification(context,currentSong,false);
            floatingLayoutManager.onPaused(data);

            WidgetUtil.onPaused(context);
        }
    }

    @Override
    public void onPlaying(Song data) {
        stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, getPlayList().indexOf(currentSong), 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
        NotificationUtil.showMusicNotification(context,currentSong,true);
        floatingLayoutManager.onPlaying(data);

        WidgetUtil.onPlaying(context);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer, final Song data) {
        data.setDuration(mediaPlayer.getDuration());
        orm.saveSong(data,sp.getUserId());

        //获取歌词
        Api.getInstance().songsDetail(data.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsObserver<DetailResponse<Song>>() {
                    @Override
                    public void onNext(DetailResponse<Song> songDetailResponse) {
                        super.onNext(songDetailResponse);
                        if (songDetailResponse != null && songDetailResponse.getData() != null && songDetailResponse.getData().getLyric() != null) {
                            //将数据设置到歌曲上
                            data.setLyric(songDetailResponse.getData().getLyric());
                            orm.saveSong(data,sp.getUserId());
                            //updateFloatingLayoutInfo();
                            sendDataReadyMessage();
                        }
                    }
                });

        //更新Android系统媒体控制中心的信息
        updateAndroidMediaInfo();

        updateFloatingLayoutInfo();

        WidgetUtil.onPrepared(context,data);
    }

    private void updateAndroidMediaInfo() {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context).asBitmap().load(ImageUtil.getImageURI(currentSong.getBanner())).apply(options).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                PlayListManagerImpl.this.albumBitmap = resource;
                updateMediaInfo();
            }
        });
    }

    private void updateFloatingLayoutInfo() {
        floatingLayoutManager.update(currentSong);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (getLoopModel() == MODEL_LOOP_ONE) {
            //如果是单曲循环，就不会处理了
            //因为我们使用了MediaPlayer的循环模式
            //如果没有使用它，那就要在这里继续播放当前音乐
            //但这样理论上来说要更耗费资源(只是理论，具体的暂时没研究)

        } else {
            //其他模式，就要播放下一首
            Song next = next();
            if (next != null) {
                play(next);
            }


        }
    }

    @Override
    public void onError(MediaPlayer mp, int what, int extra) {

    }


    @Override
    public void destroy() {
        if (notificationMusicReceiver != null) {
            context.unregisterReceiver(notificationMusicReceiver);
            notificationMusicReceiver=null;
        }
    }

    @Override
    public void nextPlay(Song song) {
        int index = datum.indexOf(currentSong);
        if (index != -1) {
            //先移除原来的歌曲，可能存在于列表
            datum.remove(song);
            datum.add(index+1,song);
        } else {
            throw new IllegalArgumentException("Can't find current song!");

        }
    }


    /**
     * 用来将事件转换到主线程
     */
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DATA_READY:
                    for (PlayListListener listener : listeners) {
                        listener.onDataReady(currentSong);
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    };

    //OnFloatingListener
    @Override
    public void onPlayClick() {
        playOrPause();
    }

    @Override
    public void onPrevious() {
        play(PlayListManagerImpl.this.previous());
    }

    @Override
    public void onNext() {
        play(PlayListManagerImpl.this.next());
    }

    //end OnFloatingListener

    public class MediaSessionCallback extends MediaSessionCompat.Callback {

        public MediaSessionCallback() {
        }

        @Override
        public void onPlay() {
            super.onPlay();
            PlayListManagerImpl.this.resume();
        }

        @Override
        public void onPause() {
            super.onPause();
            PlayListManagerImpl.this.pause();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            play(PlayListManagerImpl.this.previous());
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            play(PlayListManagerImpl.this.next());
        }

    }



}
