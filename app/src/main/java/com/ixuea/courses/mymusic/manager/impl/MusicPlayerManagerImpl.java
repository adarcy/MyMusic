package com.ixuea.courses.mymusic.manager.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.listener.OnMusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by smile on 2018/5/28.
 */

public class MusicPlayerManagerImpl implements MusicPlayerManager, MediaPlayer.OnCompletionListener {
    /**
     * Handler通知事件类型常量
     */
    public static final int MSG_PROGRESS = 0;
    private static final int MSG_PLAYING = 10;
    private static final int MSG_PAUSE = 20;
    private static final int MSG_PREPARE = 30;
    private static final int MSG_COMPLETION = 40;
    private static final int MSG_ERROR = 50;
    private static final long DEFAULT_TIME = 16;
    private static MusicPlayerManagerImpl manager;

    private final Context context;

    /**
     * 媒体播放器
     */
    private MediaPlayer player;

    /**
     * 播放器状态监听器
     */
    private List<OnMusicPlayerListener> listeners = new ArrayList<>();

    /**
     * 时间任务，用来发布播放器进度
     */
    private TimerTask timerTask;
    private Timer timer;
    private Song data;


    public static synchronized MusicPlayerManager getInstance(Context context) {
        if (manager == null) {
            manager = new MusicPlayerManagerImpl(context);
        }
        return manager;
    }

    @Override
    public void play(String uri,Song data) {
        try {
            this.data=data;
            player.reset();
            player.setDataSource(uri);
            player.prepare();
            player.start();

            handler.obtainMessage(MSG_PLAYING).sendToTarget();

            startPublishProgressService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            player.pause();
            handler.obtainMessage(MSG_PAUSE).sendToTarget();
            stopPublishProgressService();
        }
    }


    @Override
    public void resume() {
        if (!isPlaying()) {
            player.start();
            handler.obtainMessage(MSG_PLAYING).sendToTarget();
            startPublishProgressService();
        }
    }

    @Override
    public void seekTo(int progress) {
        player.seekTo(progress);
    }

    @Override
    public void addOnMusicPlayerListener(OnMusicPlayerListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        //发布一次当前的状态，目的是让新添加的监听器能够获取上一次的状态
        if (player != null) {
            //handler.obtainMessage(MSG_PREPARE).sendToTarget();
            if (isPlaying()) {
                handler.obtainMessage(MSG_PLAYING).sendToTarget();
            } else {
                handler.obtainMessage(MSG_PAUSE).sendToTarget();
            }
        }
    }

    @Override
    public void removeOnMusicPlayerListener(OnMusicPlayerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void destroy() {
        if (player != null) {
            if (isPlaying()) {
                player.stop();
            }
            player.reset();
        }
    }

    @Override
    public void setLooping(boolean looping) {
        player.setLooping(looping);
    }

    private MusicPlayerManagerImpl(Context context) {
        this.context = context;
        player = new MediaPlayer();
        initListener();
    }

    private void initListener() {
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                handler.obtainMessage(MSG_PREPARE).sendToTarget();
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                handler.obtainMessage(MSG_ERROR,what,extra).sendToTarget();
                return false;
            }
        });

        player.setOnCompletionListener(this);
    }

    /**
     * 用来将事件转换到主线程
     */
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS:
                    publishProgress();
                    break;
                case MSG_PLAYING:
                    for (OnMusicPlayerListener listener : listeners) {
                        listener.onPlaying(data);
                    }
                    break;
                case MSG_PAUSE:
                    for (OnMusicPlayerListener listener : listeners) {
                        listener.onPaused(data);
                    }
                    break;

                case MSG_PREPARE:
                    for (OnMusicPlayerListener listener : listeners) {
                        listener.onPrepared(player,data);
                    }
                    break;

                case MSG_COMPLETION:
                    for (OnMusicPlayerListener listener : listeners) {
                        listener.onCompletion(player);
                    }
                case MSG_ERROR:
                    for (OnMusicPlayerListener listener : listeners) {
                        listener.onError(player,msg.arg1,msg.arg2);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void publishProgress() {
        int currentPosition = player.getCurrentPosition();
        int duration = player.getDuration();
        for (OnMusicPlayerListener listener : listeners) {
            listener.onProgress(currentPosition, duration);
        }
    }

    private void startPublishProgressService() {
        cancelTask();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.obtainMessage(MSG_PROGRESS).sendToTarget();
            }
        };
        timer = new Timer();

        timer.schedule(timerTask, 0, DEFAULT_TIME);
    }


    private void stopPublishProgressService() {
        cancelTask();
    }

    private void cancelTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        handler.obtainMessage(MSG_COMPLETION).sendToTarget();
    }
}
