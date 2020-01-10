package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.domain.MeUI;
import com.ixuea.courses.mymusic.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by smile on 2018/6/5.
 */

public class MeAdapter extends BaseExpandableListAdapter {
    private OnMeListener onMeListener;
    private final Context context;
    private final LayoutInflater inflater;
    private List<MeUI> datas = new ArrayList<>();

    public MeAdapter(Context context) {
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return datas.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return datas.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_music_title, parent, false);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        viewHolder.setData(getGroupData(groupPosition),isExpanded);
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_me_list, parent, false);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }
        viewHolder.setData(getChildData(groupPosition,childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public MeUI getGroupData(int position) {
        return datas.get(position);
    }

    public com.ixuea.courses.mymusic.domain.List getChildData(int position, int childPosition) {
        return datas.get(position).getList().get(childPosition);
    }

    public void setData(ArrayList<MeUI> data) {
        this.datas.clear();
        this.datas.addAll(data);
        notifyDataSetChanged();
    }

    class GroupViewHolder{

        private final TextView tv_title;
        private final ImageView iv_more;

        public GroupViewHolder(View view) {
            tv_title=view.findViewById(R.id.tv_title);
            iv_more=view.findViewById(R.id.iv_more);
        }

        public void setData(MeUI data, boolean isExpanded) {
            tv_title.setText(data.getTitle());
            iv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMeListener.onListGroupSettingsClick();
                }
            });
        }
    }

    class ChildViewHolder{
        private final ImageView iv_icon;
        private final TextView tv_title;
        private final TextView tv_count;

        public ChildViewHolder(View view) {
            iv_icon=view.findViewById(R.id.iv_icon);
            tv_title=view.findViewById(R.id.tv_title);
            tv_count=view.findViewById(R.id.tv_count);
        }


        public void setData(com.ixuea.courses.mymusic.domain.List data) {
            ImageUtil.show((BaseActivity)context,iv_icon,data.getBanner());
            tv_title.setText(data.getTitle());
            tv_count.setText(context.getResources().getString(R.string.song_count,data.getSongs_count()));

        }
    }

    public void setOnMeListener(OnMeListener onMeListener) {
        this.onMeListener = onMeListener;
    }

    public interface OnMeListener{
        void onListGroupSettingsClick();
    }
}
