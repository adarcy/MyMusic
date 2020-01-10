package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.view.LyricLineView;

public class LyricAdapter extends BaseQuickRecyclerViewAdapter<Object> {
    private int index;
    private boolean isAccurate;

    public LyricAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, int position, Object data) {
        //使用TextView的实现
        //if (data instanceof String) {
        //    //字符串，用来填充占位符
        //    holder.setText(R.id.tv,"");
        //} else {
        //    //真实数据
        //    Line d= (Line) data;
        //    holder.setText(R.id.tv,d.getLineLyrics());
        //}
        //
        //
        //if (index == position) {
        //    //选中行
        //    holder.setTextColorRes(R.id.tv,R.color.main_color);
        //} else {
        //    //未选中
        //    holder.setTextColorRes(R.id.tv,R.color.lyric_text_color);
        //}

        //使用自定义View的实现
        LyricLineView llv = (LyricLineView) holder.findViewById(R.id.llv);

        if (data instanceof String) {
            //字符串，用来填充占位符
            llv.setLine(null);
            llv.setAccurate(false);
        } else {
            //真实数据
            llv.setLine((Line) data);
            llv.setAccurate(isAccurate);
        }

        if (index == position) {
            //选中行
            llv.setLineSelected(true);
        } else {
            //未选中
            llv.setLineSelected(false);
        }

    }

    public void setSelectedIndex(int index) {
        //先刷新上一行
        notifyItemChanged(this.index);

        this.index=index;

        //刷新当前行
        notifyItemChanged(this.index);
    }

    public void setAccurate(boolean accurate) {
        isAccurate = accurate;
    }
}
