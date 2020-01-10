package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Advertisement;
import com.ixuea.courses.mymusic.domain.List;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.StringUtil;

/**
 * 推荐页面Adapter
 * 这里实现三种布局
 * 歌单，单曲，广告
 * Created by smile on 2018/5/26.
 */

public class RecommendAdapter extends BaseRecyclerViewAdapter<Object, RecommendAdapter.BaseViewHolder> {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_LIST = 1;
    public static final int TYPE_SONG = 2;
    public static final int TYPE_ADVERTISEMENT = 3;

    public RecommendAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecommendAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里创建不同的ViewHolder，然后绑定的数据的时候就很简单了
        switch (viewType) {
            case TYPE_TITLE:
                return new TitleViewHolder(getInflater().inflate(R.layout.item_title, parent, false));
            case TYPE_LIST:
                return new ListViewHolder(getInflater().inflate(R.layout.item_list, parent, false));
            case TYPE_SONG:
                return new SongViewHolder(getInflater().inflate(R.layout.item_song, parent, false));
        }
        return new AdvertisementViewHolder(getInflater().inflate(R.layout.item_advertisement, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        Object data = getData(position);
        if (data instanceof String) {
            //标题
            return TYPE_TITLE;
        } else if (data instanceof List) {
            //歌单
            return TYPE_LIST;
        } else if (data instanceof Song) {
            //单曲
            return TYPE_SONG;
        }

        //广告
        return TYPE_ADVERTISEMENT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendAdapter.BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        //绑定数据直接调用bindData方法
        //具体的每个类自己实现，爱咋地就咋地
        holder.bindData(getData(position));
    }

    @Override
    public int setSpanSizeLookup(int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case RecommendAdapter.TYPE_TITLE:
                return 3;
            case RecommendAdapter.TYPE_LIST:
                return 1;
            case RecommendAdapter.TYPE_SONG:
                return 3;
        }
        return 3;
    }

    abstract class BaseViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindData(Object data);
    }

    private class TitleViewHolder extends BaseViewHolder {
        private TextView tv_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) findViewById(R.id.tv_title);
        }

        @Override
        public void bindData(Object data) {
            tv_title.setText(data.toString());
        }
    }

    private class ListViewHolder extends BaseViewHolder {
        private ImageView iv_banner;
        private TextView tv_count;
        private TextView tv_title;

        public ListViewHolder(View itemView) {
            super(itemView);
            iv_banner = (ImageView) findViewById(R.id.iv_banner);
            tv_count = (TextView) findViewById(R.id.tv_count);
            tv_title = (TextView) findViewById(R.id.tv_title);
        }

        @Override
        public void bindData(Object data) {
            List d = (List) data;
            ImageUtil.show((Activity) context, iv_banner, d.getBanner());
            tv_count.setText(StringUtil.formatCount(d.getClicks_count()));
            tv_title.setText(d.getTitle());
        }
    }

    private class SongViewHolder extends BaseViewHolder {
        private final ImageView iv_avatar;
        private final TextView tv_nickname;
        private ImageView iv_icon;
        private TextView tv_title;
        private TextView tv_comment_count;

        public SongViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) findViewById(R.id.iv_icon);
            iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_nickname = (TextView) findViewById(R.id.tv_nickname);
            tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
        }

        @Override
        public void bindData(Object data) {
            Song d = (Song) data;
            ImageUtil.show((Activity) context, iv_icon, d.getBanner());
            tv_title.setText(d.getTitle());
            tv_nickname.setText(d.getArtist().getNickname());
            tv_comment_count.setText(StringUtil.formatCount(d.getComments_count()));

            ImageUtil.show((Activity) context, iv_avatar, d.getArtist().getAvatar());
        }
    }

    private class AdvertisementViewHolder extends BaseViewHolder {
        private ImageView iv_icon;
        private TextView tv_title;

        public AdvertisementViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) findViewById(R.id.iv_icon);
            tv_title = (TextView) findViewById(R.id.tv_title);
        }

        @Override
        public void bindData(Object data) {
            Advertisement d = (Advertisement) data;
            ImageUtil.show((Activity) context, iv_icon, d.getBanner());
            tv_title.setText(d.getTitle());
        }
    }

}
