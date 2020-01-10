package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.callbck.MyDownloadListener;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.event.DownloadStatusChanged;
import com.ixuea.courses.mymusic.util.FileUtil;
import com.ixuea.courses.mymusic.util.OrmUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.SoftReference;

import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 * 正在下载adapter
 * Created by smile on 2018/5/26.
 */

public class DownloadingAdapter extends BaseRecyclerViewAdapter<DownloadInfo,DownloadingAdapter.ViewHolder> {

    private final OrmUtil orm;
    private final DownloadManager downloadManager;

    public DownloadingAdapter(Context context, OrmUtil orm, final DownloadManager downloadManager) {
        super(context);
        this.orm=orm;
        this.downloadManager=downloadManager;

        //条目点击事件
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                DownloadInfo downloadInfo = getData(position);
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                    case DownloadInfo.STATUS_PAUSED:
                    case DownloadInfo.STATUS_ERROR:

                        //resume downloadInfo
                        downloadManager.resume(downloadInfo);
                        break;

                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    case DownloadInfo.STATUS_WAIT:
                        //pause downloadInfo
                        downloadManager.pause(downloadInfo);
                        break;
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new ViewHolder(getInflater().inflate(R.layout.item_downloading, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        DownloadInfo downloadInfo = getData(position);

        //获取业务数据，如：名称
        Song song = orm.findSongById(downloadInfo.getId());
        holder.bindBaseData(song);

    //下载数据
        holder.bindData(downloadInfo,position);
    }


    public class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {


        private final TextView tv_title;
        private final TextView tv_pause_info;
        private final TextView tv_download_info;
        private final ImageView iv_delete;
        private final ProgressBar pb;
        private final LinearLayout ll_downloading_container;
        private DownloadInfo downloadInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_pause_info=itemView.findViewById(R.id.tv_pause_info);
            tv_download_info=itemView.findViewById(R.id.tv_download_info);
            iv_delete=itemView.findViewById(R.id.iv_delete);
            pb=itemView.findViewById(R.id.pb);
            ll_downloading_container=itemView.findViewById(R.id.ll_downloading_container);
        }

        public void bindBaseData(Song song) {
            tv_title.setText(song.getTitle());
        }

        public void bindData(final DownloadInfo downloadInfo, int position) {
            this.downloadInfo=downloadInfo;
            //设置回调
            downloadInfo.setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {
                //  Call interval about one second
                @Override
                public void onRefresh() {
                    if (getUserTag() != null && getUserTag().get() != null) {
                        ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                        viewHolder.refresh();
                    }
                }
            });

            refresh();

            //删除按钮点击事件
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadManager.remove(downloadInfo);
                }
            });
        }

        private void refresh() {
            switch (downloadInfo.getStatus()) {
                case DownloadInfo.STATUS_PAUSED:
                case DownloadInfo.STATUS_ERROR:
                    ll_downloading_container.setVisibility(View.GONE);
                    tv_pause_info.setVisibility(View.VISIBLE);

                    tv_pause_info.setText(R.string.click_download);
                    try {
                        pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tv_download_info.setText(context.getString(R.string.download_progress, FileUtil.formatFileSize(downloadInfo.getProgress()),FileUtil.formatFileSize(downloadInfo.getProgress())));
                    break;

                case DownloadInfo.STATUS_DOWNLOADING:
                case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    ll_downloading_container.setVisibility(View.VISIBLE);
                    tv_pause_info.setVisibility(View.GONE);

                    try {
                        pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tv_download_info.setText(context.getString(R.string.download_progress, FileUtil.formatFileSize(downloadInfo.getProgress()),FileUtil.formatFileSize(downloadInfo.getSize())));
                    break;
                case DownloadInfo.STATUS_COMPLETED:
                    //通知下载成功了
                    publishDownloadSuccessStatus();

                    //移除当前列表
                    removeData(downloadInfo);
                    notifyDataSetChanged();

                case DownloadInfo.STATUS_REMOVED:
                    removeData(downloadInfo);
                    notifyDataSetChanged();
                    break;
                case DownloadInfo.STATUS_WAIT:
                    ll_downloading_container.setVisibility(View.VISIBLE);
                    tv_pause_info.setVisibility(View.GONE);

                    tv_download_info.setText(R.string.wait_download);
                    pb.setProgress(0);
                    break;
            }

        }

        private void publishDownloadSuccessStatus() {
            //publish download success info.
            EventBus.getDefault().post(new DownloadStatusChanged(downloadInfo));
        }
    }
}
