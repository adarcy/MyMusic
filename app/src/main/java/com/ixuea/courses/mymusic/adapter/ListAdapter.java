package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.domain.List;
import com.ixuea.courses.mymusic.util.ImageUtil;


/**
 * 歌单列表adapter
 * Created by smile on 2018/5/26.
 */

public class ListAdapter extends BaseQuickRecyclerViewAdapter<List> {

    public ListAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, final int position, List data) {
        ImageUtil.show((BaseActivity)context, (ImageView) holder.getView(R.id.iv_icon),data.getBanner());
        holder.setText(R.id.tv_title,data.getTitle());
        holder.setText(R.id.tv_count,context.getResources().getString(R.string.song_count,data.getSongs_count()));
    }
}
