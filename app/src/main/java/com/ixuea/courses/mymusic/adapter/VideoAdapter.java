package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Video;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;


/**
 * 视频列表adapter
 * Created by smile on 2018/5/26.
 */

public class VideoAdapter extends BaseQuickRecyclerViewAdapter<Video> {



    public VideoAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, int position, final Video data) {
        ImageUtil.show((Activity) context,(ImageView) holder.findViewById(R.id.iv_icon),data.getBanner());
        ImageUtil.showCircle((Activity) context,(ImageView) holder.findViewById(R.id.iv_avatar),data.getUser().getAvatar());

        holder.setText(R.id.tv_nickname,data.getUser().getNickname());
        holder.setText(R.id.tv_title,data.getTitle());
        holder.setText(R.id.tv_clicks_count,String.valueOf(data.getClicks_count()));
        holder.setText(R.id.tv_time, TimeUtil.formatMSTime((int) data.getDuration()));
        holder.setText(R.id.tv_likes_count, String.valueOf(data.getLikes_count()));
        holder.setText(R.id.tv_comments_count, String.valueOf(data.getComments_count()));

    }


}
