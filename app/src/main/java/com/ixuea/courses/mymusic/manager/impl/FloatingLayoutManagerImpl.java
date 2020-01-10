package com.ixuea.courses.mymusic.manager.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.listener.OnFloatingListener;
import com.ixuea.courses.mymusic.manager.FloatingLayoutManager;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.NotificationUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;
import com.ixuea.courses.mymusic.view.FloatingLinearLayoutView;

import java.lang.reflect.Field;

/**
 * Created by smile on 2018/6/3.
 */

public class FloatingLayoutManagerImpl implements FloatingLayoutManager, FloatingLinearLayoutView.OnTouchDragListener {
    public static final String TAG = "TAG";
    private static final int GLOBAL_LYRIC_MARGIN = 0;
    private final SharedPreferencesUtil sp;
    private boolean isShowLyric;

    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private FloatingLinearLayoutView floatingLayout;

    private static FloatingLayoutManager manager;
    private final Context context;
    private boolean isLyricLock;
    private BroadcastReceiver unlockLyricReceiver;
    private OnFloatingListener onFloatingListener;
    private int statusBarHeight;

    public FloatingLayoutManagerImpl(Context context) {
        this.context=context;
        sp = SharedPreferencesUtil.getCurrentInstance();
        isLyricLock =sp.isLyricLock();
        isShowLyric =sp.isShowLyric();

        if (isShowLyric) {
            initWindowManager();
            initGlobalLyricView();
            if (isLyricLock) {
                lock();
            }
        }
    }

    public static synchronized FloatingLayoutManager getInstance(Context context) {
        if (manager == null) {
            manager = new FloatingLayoutManagerImpl(context);
        }
        return manager;
    }


    @Override
    public void show() {
        sp.setShowLyric(true);
        isShowLyric=true;

        initWindowManager();
        initGlobalLyricView();
    }

    @Override
    public void hide() {
        sp.setShowLyric(false);
        isShowLyric=false;

        removeFloatingLayout();
    }

    @Override
    public boolean isShowing() {
        return isShowLyric;
    }

    @Override
    public void update(Song song) {
        if (floatingLayout != null) {
            floatingLayout.update(song);
        }
    }

    @Override
    public void onPlaying(Song data) {
        if (floatingLayout != null) {
            floatingLayout.setPlay(true);
        }
    }

    @Override
    public void onPaused(Song data) {
        if (floatingLayout != null) {
            floatingLayout.setPlay(false);
        }
    }

    @Override
    public void onProgress(long progress, long total) {
        if (floatingLayout != null) {
            floatingLayout.onProgress(progress);
        }
    }

    @Override
    public void setOnFloatingListener(OnFloatingListener onFloatingListener) {
        this.onFloatingListener=onFloatingListener;
        if (floatingLayout != null) {
            floatingLayout.setOnFloatingListener(onFloatingListener);
        }
    }

    @Override
    public void tryHide() {
        if (isShowLyric) {
            floatingLayout.setAlpha(0);
        }
    }



    @Override
    public void tryShow() {
        if (isShowLyric) {
            floatingLayout.setAlpha(1);
        }
    }


    /**
     * 创建全局歌词View
     */
    private void initGlobalLyricView() {
        if (floatingLayout == null) {
            floatingLayout = new FloatingLinearLayoutView(context);
            floatingLayout.setOnTouchDragListener(this);
            floatingLayout.setOnFloatingListener(onFloatingListener);
            floatingLayout.setOnCloseListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
            floatingLayout.setOnLogoClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMusicPlayActivity();
                }
            });

            floatingLayout.setOnLockClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLyricLock =true;
                    lock();
                    sp.setLyricLock(true);
                }
            });

        }

        if (floatingLayout.getParent()==null) {
            windowManager.addView(floatingLayout, layoutParams);
        }

    }

    private void lock() {
        isLyricLock=true;
        sp.setLyricLock(true);

        setWindowLockStatus();
        floatingLayout.simpleStyle();
        updateWindow();
        showUnlockLyricNotification();
        registerUnlockLyricReceiver();

    }

    private void registerUnlockLyricReceiver() {
        if (unlockLyricReceiver == null) {
            unlockLyricReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (Consts.ACTION_UNLOCK_LYRIC.equals(intent.getAction())) {
                        unlock();
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Consts.ACTION_UNLOCK_LYRIC);
            context.registerReceiver(unlockLyricReceiver,intentFilter);
        }
    }

    private void showUnlockLyricNotification() {
        NotificationUtil.showUnlockLyricNotification(context);
    }

    private void unlock() {
        isLyricLock=false;
        sp.setLyricLock(false);

        setWindowLockStatus();
        floatingLayout.normalStyle();
        updateWindow();
        clearUnlockLyricNotification();
        unregisterUnlockLyricReceiver();
    }

    private void unregisterUnlockLyricReceiver() {
        if (unlockLyricReceiver != null) {
            context.unregisterReceiver(unlockLyricReceiver);
            unlockLyricReceiver=null;
        }
    }

    private void clearUnlockLyricNotification() {
        NotificationUtil.clearUnlockLyricNotification(context);
    }

    private void updateWindow() {
        windowManager.updateViewLayout(floatingLayout, layoutParams);
    }

    private void setWindowLockStatus() {
        // 设置窗体焦点及触摸：
        if (isLyricLock) {
            //已经锁定了，将当前窗口的事件还是传递给后面的View
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        } else {
            //没有锁定
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
    }

    private void startMusicPlayActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Consts.ACTION_MUSIC_PLAYER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void removeFloatingLayout() {
        windowManager.removeView(floatingLayout);
    }

    /**
     * 创建窗口
     */
    private void initWindowManager() {
        if (windowManager == null) {
            // 取得系统窗体
            windowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);

            // 窗体的布局样式
            layoutParams = new WindowManager.LayoutParams();
            // 设置窗体显示类型——TYPE_SYSTEM_ALERT,系统提示
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            // 设置显示的模式
            layoutParams.format = PixelFormat.RGBA_8888;
            // 设置对齐的方法
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            // 设置窗体宽度和高度
            DisplayMetrics dm = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(dm);
            layoutParams.width = dm.widthPixels - GLOBAL_LYRIC_MARGIN;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.y = sp.getGlobalLyricY();

            setWindowLockStatus();
        }
    }

    @Override
    public void onMove(int y) {
        layoutParams.y=y-getStatusBarHeight();
        updateWindow();
        sp.setGlobalLyricY(layoutParams.y);
    }

    /**
     * 获得状态栏的高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
                Log.d(TAG, "getStatusBarHeight: "+x+","+statusBarHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

}
