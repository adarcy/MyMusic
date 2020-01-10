package com.ixuea.courses.mymusic.manager;

import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.listener.OnFloatingListener;

/**
 * Created by smile on 2018/6/3.
 */

public interface FloatingLayoutManager {
    /**
     * 显示桌面歌词
     */
    void show();

    /**
     * 隐藏桌面歌词
     */
    void hide();

    /**
     * 桌面歌词是否显示
     */
    boolean isShowing();

    /**
     * 更新数据
     */
    void update(Song song);

    /**
     * 当播放时
     * @param data
     */
    void onPlaying(Song data);

    /**
     * 暂停时
     * @param data
     */
    void onPaused(Song data);

    /**
     * 播放进度回调时
     * @param progress
     * @param total
     */
    void onProgress(long progress, long total);

    /**
     * 尝试隐藏，为什么是尝试隐藏呢？因为有可能当前都没有显示
     */
    void tryHide();

    /**
     * 尝试显示
     */
    void tryShow();

    /**
     * 设置监听器
     * @param onFloatingListener
     */
    void setOnFloatingListener(OnFloatingListener onFloatingListener);

}
