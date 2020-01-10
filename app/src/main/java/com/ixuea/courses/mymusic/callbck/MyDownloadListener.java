package com.ixuea.courses.mymusic.callbck;

import java.lang.ref.SoftReference;

import cn.woblog.android.downloader.callback.AbsDownloadListener;
import cn.woblog.android.downloader.exception.DownloadException;

/**
 * 下载监听器，将所有回调都调用onRefresh方法
 * Created by smile on 2018/6/8.
 */

public abstract class MyDownloadListener extends AbsDownloadListener {

    public MyDownloadListener() {
        super();
    }

    public MyDownloadListener(SoftReference<Object> userTag) {
        super(userTag);
    }

    @Override
    public void onStart() {
        onRefresh();
    }

    public abstract void onRefresh();

    @Override
    public void onWaited() {
        onRefresh();
    }

    @Override
    public void onDownloading(long progress, long size) {
        onRefresh();
    }

    @Override
    public void onRemoved() {
        onRefresh();
    }

    @Override
    public void onDownloadSuccess() {
        onRefresh();
    }

    @Override
    public void onDownloadFailed(DownloadException e) {
        onRefresh();
    }

    @Override
    public void onPaused() {
        onRefresh();
    }
}
