package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Topic;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * 好友列表adapter
 * Created by smile on 2018/5/26.
 */

public class TopicAdapter extends BaseQuickRecyclerViewAdapter<Topic> {

    public TopicAdapter(Context context, int layoutId) {
        super(context, layoutId);

    }

    @Override
    protected void bindData(ViewHolder holder, int position, final Topic data) {
        ImageUtil.show((Activity) context, (ImageView) holder.getView(R.id.iv_icon),data.getBanner());
        holder.setText(R.id.tv_title,"#"+data.getTitle()+"#");
        holder.setText(R.id.tv_info,context.getResources().getString(R.string.join_count,data.getJoins_count()));
    }

}
