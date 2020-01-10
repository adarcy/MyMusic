package com.ixuea.courses.mymusic.listener;

import android.media.MediaPlayer;

import com.ixuea.courses.mymusic.domain.Song;

/**
 * Created by smile on 2018/5/28.
 */

public interface OnMusicPlayerListener {
    /**
     * 进度通知
     * @param progress
     * @param total
     */
    void onProgress(long progress, long total);

    /**
     * 已经暂停了
     */
    void onPaused(Song data);

    /**
     * 已经播放了
     */
    void onPlaying(Song data);

    /**
     * 播放器初始化完毕
     * @param mediaPlayer
     */
    void onPrepared(MediaPlayer mediaPlayer, Song data);

    /**
     * 当前这首歌播放完毕了
     * @param mediaPlayer
     */
    void onCompletion(MediaPlayer mediaPlayer);


    /**
     * 当发生错误时
     * @param mp
     * @param what
     * @param extra
     */
    void onError(MediaPlayer mp, int what, int extra);
}
