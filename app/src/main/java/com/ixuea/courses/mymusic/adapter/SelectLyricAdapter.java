package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.parser.domain.Line;


/**
 * 选择歌词adapter
 * Created by smile on 2018/5/26.
 */

public class SelectLyricAdapter extends BaseQuickRecyclerViewAdapter<Line> {
    /**
     * 1表示选中，0表示没选中
     */
    private int[] selectedIndex;

    public SelectLyricAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, final int position, Line data) {
        holder.setText(R.id.tv_title,data.getLineLyrics());

        if (selectedIndex[position] == 0) {
            //没选中
            holder.setVisibility(R.id.iv_check,View.INVISIBLE);
        } else {
            //选中
            holder.setVisibility(R.id.iv_check,View.VISIBLE);
        }
    }

    @Override
    public void setData(java.util.List<Line> data) {
        super.setData(data);
        //创建一个和数据长度一样的数组
        selectedIndex=new int[data.size()];
    }

    public void setSelected(int position,boolean isSelected) {
        selectedIndex[position]=isSelected?1:0;
        notifyItemChanged(position);
    }

    public boolean isSelected(int position) {
        return selectedIndex[position]==1;
    }

    public int[] getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int[] selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
