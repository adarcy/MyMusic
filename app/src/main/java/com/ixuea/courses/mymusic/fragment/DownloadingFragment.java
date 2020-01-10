package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.DownloadingAdapter;

import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 * 下载中界面
 * Created by smile on 02/03/2018.
 */

public class DownloadingFragment extends BaseCommonFragment implements View.OnClickListener {

    RecyclerView rv;
    private DownloadingAdapter adapter;
    private DownloadManager downloadManager;
    private LinearLayout ll_download_pause_all;
    private TextView tv_pause_info;
    private LinearLayout ll_download_delete_all;
    private boolean hasDownloading;

    public static DownloadingFragment newInstance() {

        Bundle args = new Bundle();
        DownloadingFragment fragment = new DownloadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        ll_download_pause_all=findViewById(R.id.ll_download_pause_all);
        tv_pause_info=findViewById(R.id.tv_pause_info);
        ll_download_delete_all=findViewById(R.id.ll_download_delete_all);

        rv=findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

    }

    @Override
    protected void initDatas() {
        super.initDatas();
        downloadManager = DownloadService.getDownloadManager(getActivity().getApplicationContext());

        adapter = new DownloadingAdapter(getMainActivity(),orm,downloadManager);
        rv.setAdapter(adapter);

        List<DownloadInfo> allDownloading = downloadManager.findAllDownloading();
        adapter.setData(allDownloading);

        for (DownloadInfo downloadInfo : allDownloading) {
            //如果有一个的状态是正在下载，按钮就是暂停所有
            if (downloadInfo.getStatus()==DownloadInfo.STATUS_DOWNLOADING || downloadInfo.getStatus()==DownloadInfo.STATUS_PREPARE_DOWNLOAD) {
                hasDownloading=true;
                break;
            }
        }

        setPauseOrResumeButtonStatus();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ll_download_pause_all.setOnClickListener(this);
        ll_download_delete_all.setOnClickListener(this);
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_downloading,null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_download_delete_all:
                deleteAll();
                break;

            case R.id.ll_download_pause_all:
                pauseOrResumeAll();
                break;
        }
    }

    private void pauseOrResumeAll() {
        if (hasDownloading) {
            pauseAll();
            hasDownloading =false;
        } else {
            resumeAll();
            hasDownloading =true;
        }

        setPauseOrResumeButtonStatus();
    }

    private void setPauseOrResumeButtonStatus() {
        if (hasDownloading) {
            tv_pause_info.setText(R.string.all_pause);
        } else {
            tv_pause_info.setText(R.string.all_download);
        }
    }

    private void resumeAll() {
        List<DownloadInfo> datas = adapter.getDatas();
        for (DownloadInfo downloadInfo : datas) {
            downloadManager.resumeForce(downloadInfo);
        }
    }

    private void pauseAll() {
        List<DownloadInfo> datas = adapter.getDatas();
        for (DownloadInfo downloadInfo : datas) {
            downloadManager.pauseForce(downloadInfo);
        }
    }

    private void deleteAll() {
        List<DownloadInfo> datas = adapter.getDatas();
        for (DownloadInfo downloadInfo : datas) {
            downloadManager.remove(downloadInfo);
        }
        adapter.clearData();
    }
}
