package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.fragment.SongMoreDialogFragment;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.util.OrmUtil;

import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 * 下载完成adapter
 * Created by smile on 2018/5/26.
 */

public class DownloadedAdapter extends BaseRecyclerViewAdapter<DownloadInfo,DownloadedAdapter.ViewHolder> {

    private final OrmUtil orm;
    private final DownloadManager downloadManager;
    private final FragmentManager fragmentManager;
    private final PlayListManager playListManager;

    public DownloadedAdapter(Context context, OrmUtil orm, DownloadManager downloadManager, FragmentManager fragmentManager, PlayListManager playListManager) {
        super(context);
        this.orm=orm;
        this.downloadManager=downloadManager;
        this.fragmentManager=fragmentManager;
        this.playListManager=playListManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new ViewHolder(getInflater().inflate(R.layout.item_song_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        DownloadInfo downloadInfo = getData(position);

        //获取业务数据，如：名称
        Song song = orm.findSongById(downloadInfo.getId());
        holder.bindBaseData(song,downloadInfo,position);
    }


    public class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {


        private final TextView tv_position;
        private final TextView tv_title;
        private final TextView tv_info;
        private final ImageView iv_more;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_position=itemView.findViewById(R.id.tv_position);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_info=itemView.findViewById(R.id.tv_info);
            iv_more=itemView.findViewById(R.id.iv_more);
        }

        public void bindBaseData(final Song data, final DownloadInfo downloadInfo, int position) {
            tv_position.setText(String.valueOf(position+1));

            tv_title.setText(data.getTitle());
            tv_info.setText(data.getArtist_name()+" - "+data.getAlbum_title());

            if (data.equals(playListManager.getPlayData())) {
                tv_title.setTextColor(context.getResources().getColor(R.color.main_color));
            } else {
                tv_title.setTextColor(context.getResources().getColor(R.color.text));
            }


            iv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongMoreDialogFragment.show(fragmentManager, data,true, new SongMoreDialogFragment.OnMoreListener() {
                        @Override
                        public void onCollectionClick(Song song) {
                        }

                        @Override
                        public void onDownloadClick(Song song) {

                        }

                        @Override
                        public void onDeleteClick(Song song) {
                            //从下载任务中删除
                            downloadManager.remove(downloadInfo);

                            //从当前集合中删除
                            removeData(downloadInfo);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

        }

    }
}
