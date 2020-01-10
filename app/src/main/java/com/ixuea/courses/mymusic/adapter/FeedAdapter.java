package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.activity.TopicDetailActivity;
import com.ixuea.courses.mymusic.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.domain.Feed;
import com.ixuea.courses.mymusic.listener.OnTagClickListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.TagUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

/**
 * 动态列表adapter
 * Created by smile on 2018/5/26.
 */

public class FeedAdapter extends BaseQuickRecyclerViewAdapter<Feed> {

    public FeedAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void bindData(ViewHolder holder, int position, final Feed data) {
        ImageView iv_avatar = holder.getView(R.id.iv_avatar);
        ImageUtil.showCircle((Activity) context,iv_avatar,data.getUser().getAvatar());

        TextView tv_content = holder.getView(R.id.tv_content);
        //设置后才可以点击
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());

        //链接颜色
        tv_content.setLinkTextColor(context.getResources().getColor(R.color.text_Highlight));
        tv_content.setText(TagUtil.process(context, data.getContent(), new OnTagClickListener() {
            @Override
            public void onTagClick(String content) {
                processTagClick(content);
            }
        }));

        TextView tv_nickname = holder.getView(R.id.tv_nickname);
        tv_nickname.setText(data.getUser().getNickname());

        TextView tv_time = holder.getView(R.id.tv_time);
        tv_time.setText(TimeUtil.dateTimeFormat1(data.getCreated_at()));

        //如果有图片
        RecyclerView rv = (RecyclerView) holder.findViewById(R.id.rv);
        if (data.getImages() != null && data.getImages().size() > 0) {
            rv.setVisibility(View.VISIBLE);
            rv.setHasFixedSize(true);
            rv.setNestedScrollingEnabled(false);

            int spanCount=2;
            if (data.getImages().size() > 4) {
                //如果大于4张图片，就3列显示
                spanCount=3;
            }
            GridLayoutManager layoutManager = new GridLayoutManager(context, spanCount);
            rv.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(context, R.layout.item_iamge);
            rv.setAdapter(imageAdapter);

            imageAdapter.setData(data.getImages());
        } else {
            rv.setVisibility(View.GONE);
            rv.setAdapter(null);
        }

    }

    /**
     * Tag点击，这部分可以用监听器回调到Activity中在处理
     * @param content
     */
    private void processTagClick(String content) {
        Log.d("TAG", "processTagClick: "+content);
        if (content.startsWith(Consts.MENTION)) {
            //用户详情界面
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra(Consts.NICKNAME, TagUtil.removeTag(content));
            ((BaseActivity)context).startActivity(intent);
        } else if (content.startsWith(Consts.HAST_TAG)) {
            //话题详情
            Intent intent = new Intent(context, TopicDetailActivity.class);
            intent.putExtra(Consts.TITLE, TagUtil.removeTag(content));
            ((BaseActivity)context).startActivity(intent);
        }
    }

}
