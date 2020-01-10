package com.ixuea.courses.mymusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ixuea.courses.mymusic.manager.FloatingLayoutManager;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.manager.impl.FloatingLayoutManagerImpl;
import com.ixuea.courses.mymusic.manager.impl.MusicPlayerManagerImpl;
import com.ixuea.courses.mymusic.manager.impl.PlayListManagerImpl;
import com.ixuea.courses.mymusic.util.ServiceUtil;

public class MusicPlayerService extends Service {

    private static MusicPlayerManager manager;
    private static PlayListManager playListManager;
    private static FloatingLayoutManager floatingLayoutManager;

    /**
     * 提供一个静态方法获获取Manager
     * 为什么不支持将逻辑写到Service呢？
     * 是因为操作service要么通过bindService，那么startService麻烦
     * @param context
     * @return
     */
    public static MusicPlayerManager getMusicPlayerManager(Context context) {
        startService(context);
        if (MusicPlayerService.manager == null) {
            //初始化音乐播放管理器
            MusicPlayerService.manager = MusicPlayerManagerImpl.getInstance(context);
        }
        return manager;
    }

    /**
     * 获取一个PlayListManager对象
     * @param context
     * @return
     */
    public static PlayListManager getPlayListManager(Context context) {
        startService(context);

        if (MusicPlayerService.playListManager == null) {
            //初始化列表管理器
            MusicPlayerService.playListManager = PlayListManagerImpl.getInstance(context);
        }
        return playListManager;
    }

    public static FloatingLayoutManager getFloatingLayoutManager(Context context) {
        startService(context);

        if (MusicPlayerService.floatingLayoutManager == null) {
            //初始化浮动窗口管理器
            MusicPlayerService.floatingLayoutManager = FloatingLayoutManagerImpl.getInstance(context);
        }
        return floatingLayoutManager;
    }

    private static void startService(Context context) {
        if (!ServiceUtil.isServiceRunning(context)) {
            //如果当前Service没有引用就要启动它
            Intent downloadSvr = new Intent(context, MusicPlayerService.class);
            context.startService(downloadSvr);
        }
    }

    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
