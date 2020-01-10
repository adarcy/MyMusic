package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.fragment.SongMoreDialogFragment;
import com.ixuea.courses.mymusic.manager.PlayListManager;

import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;


/**
 * 歌曲列表adapter
 * Created by smile on 2018/5/26.
 */

public class SongAdapter extends BaseQuickRecyclerViewAdapter<Song> {


    private final PlayListManager playList;
    private final FragmentManager fragmentManager;
    private OnSongListener onSongListener;
    private DownloadManager downloadManager;

    /**
     * 是否是我的歌单
     */
    private boolean isMySheet;

    public SongAdapter(Context context, int layoutId, FragmentManager fragmentManager, PlayListManager playList, DownloadManager downloadManager) {
        super(context, layoutId);
        this.playList=playList;
        this.fragmentManager=fragmentManager;
        this.downloadManager=downloadManager;
    }

    @Override
    protected void bindData(ViewHolder holder, int position, final Song data) {
        holder.setText(R.id.tv_position,String.valueOf(position+1));
        holder.setText(R.id.tv_title,data.getTitle());

        holder.setText(R.id.tv_info,data.getArtist_name()+" - "+data.getAlbum_title());

        //当前播放
        if (data.equals(playList.getPlayData())) {
            holder.setTextColorRes(R.id.tv_title, R.color.main_color);
        } else {
            holder.setTextColorRes(R.id.tv_title,R.color.text);
        }

        //是否下载
        DownloadInfo downloadInfo = downloadManager.getDownloadById(data.getId());
        if (downloadInfo != null && downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED) {
            //下载完成了
            holder.setVisibility(R.id.iv_downloaded, View.VISIBLE);
        } else {
            holder.setVisibility(R.id.iv_downloaded,View.GONE);
        }

        holder.setOnClickListener(R.id.iv_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 如果是我的歌单，就显示删除按钮
                 */
                SongMoreDialogFragment.show(fragmentManager, data,isMySheet, new SongMoreDialogFragment.OnMoreListener() {
                    @Override
                    public void onCollectionClick(Song song) {
                        collectionSong(song);
                    }

                    @Override
                    public void onDownloadClick(Song song) {
                        if (onSongListener != null) {
                            onSongListener.onDownloadClick(song);
                        }
                    }

                    @Override
                    public void onDeleteClick(Song song) {
                        //从本地列表中删除
                        removeData(song);
                        //回调接口，还要从服务端删除
                        if (onSongListener != null) {
                            onSongListener.onDeleteClick(song);
                        }
                    }
                });
            }
        });
    }

    private void collectionSong(Song song) {
        if (onSongListener != null) {
            onSongListener.onCollectionClick(song);
        }
    }

    public void setOnSongListener(OnSongListener onSongListener) {
        this.onSongListener = onSongListener;
    }

    public void setMySheet(boolean isMySheet) {
        this.isMySheet=isMySheet;
    }

    public interface OnSongListener{
        void onCollectionClick(Song song);
        void onDownloadClick(Song song);
        void onDeleteClick(Song song);
    }
}
