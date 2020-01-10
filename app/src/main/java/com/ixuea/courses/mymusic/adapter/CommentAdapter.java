package com.ixuea.courses.mymusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.activity.TopicDetailActivity;
import com.ixuea.courses.mymusic.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.domain.Comment;
import com.ixuea.courses.mymusic.listener.OnTagClickListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.TagUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;


/**
 * 评论列表adapter
 * Created by smile on 2018/5/26.
 */

public class CommentAdapter extends BaseRecyclerViewAdapter<Object,CommentAdapter.BaseViewHolder> {
    public static final int TYPE_TITLE = 0;
    private static final int TYPE_COMMENT = 1;
    private OnCommentAdapter onCommentAdapter;


    public void setOnCommentAdapter(OnCommentAdapter onCommentAdapter) {
        this.onCommentAdapter = onCommentAdapter;
    }

    public CommentAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public CommentAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_TITLE==viewType) {
            return  new CommentAdapter.TitleViewHolder(getInflater().inflate(R.layout.item_comment_title, parent, false));
        }
        return  new CommentAdapter.CommentViewHolder(getInflater().inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Object data = getData(position);

        holder.bindData(data);
    }

    @Override
    public int getItemViewType(int position) {
        Object data = getData(position);
        if (data instanceof String) {
            //标题
            return TYPE_TITLE;
        }

        //评论
        return TYPE_COMMENT;
    }

    class BaseViewHolder extends BaseRecyclerViewAdapter.ViewHolder{

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public void bindData(Object data) {

        }
    }

    class TitleViewHolder extends BaseViewHolder{

        private final TextView tv_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            tv_title= (TextView) findViewById(R.id.tv_title);
        }

        @Override
        public void bindData(Object data) {
            tv_title.setText(data.toString());
        }
    }

    class CommentViewHolder extends BaseViewHolder{

        private final ImageView iv_avatar;
        private final ImageView iv_like;
        private final TextView tv_nickname;
        private final TextView tv_like_count;
        private final TextView tv_time;
        private final TextView tv_content;
        private final TextView tv_reply_content;
        private final LinearLayout ll_like_container;
        private final CardView cv_reply_container;

        public CommentViewHolder(View itemView) {
            super(itemView);
            iv_avatar= (ImageView) findViewById(R.id.iv_avatar);
            iv_like= (ImageView) findViewById(R.id.iv_like);
            tv_nickname= (TextView) findViewById(R.id.tv_nickname);
            tv_like_count= (TextView) findViewById(R.id.tv_like_count);
            tv_time= (TextView) findViewById(R.id.tv_time);
            tv_content= (TextView) findViewById(R.id.tv_content);
            tv_reply_content= (TextView) findViewById(R.id.tv_reply_content);

            ll_like_container= (LinearLayout) findViewById(R.id.ll_like_container);
            cv_reply_container= (CardView) findViewById(R.id.cv_reply_container);
        }

        @Override
        public void bindData(final Object data) {
            super.bindData(data);
            Comment c= (Comment) data;
            ImageUtil.showCircle((Activity) context,iv_avatar,c.getUser().getAvatar());

            //设置后才可以点击
            tv_content.setMovementMethod(LinkMovementMethod.getInstance());

            //链接颜色
            tv_content.setLinkTextColor(context.getResources().getColor(R.color.text_Highlight));
            //tv_content.setHighlightColor(context.getResources().getColor(R.color.text_Highlight));
            tv_content.setText(TagUtil.process(context, c.getContent(), new OnTagClickListener() {
                @Override
                public void onTagClick(String content) {
                    processTagClick(content);
                }
            }));

            tv_nickname.setText(c.getUser().getNickname());
            tv_time.setText(TimeUtil.dateTimeFormat1(((Comment) data).getCreated_at()));

            tv_like_count.setText(String.valueOf(c.getLikes_count()));
            if (c.isLiked()) {
                iv_like.setImageResource(R.drawable.ic_comment_liked);
                tv_like_count.setTextColor(context.getResources().getColor(R.color.main_color));
            } else {
                iv_like.setImageResource(R.drawable.ic_comment_like);
                tv_like_count.setTextColor(context.getResources().getColor(R.color.text_dark));
            }

            //回复的评论
            if (c.getParent() == null) {
                cv_reply_container.setVisibility(View.GONE);
            } else {
                cv_reply_container.setVisibility(View.VISIBLE);

                tv_reply_content.setMovementMethod(LinkMovementMethod.getInstance());
                tv_reply_content.setLinkTextColor(context.getResources().getColor(R.color.text_Highlight));
                tv_reply_content.setText(TagUtil.process(context,context.getString(R.string.reply_comment_show,c.getParent().getUser().getNickname(),c.getParent().getContent()), new OnTagClickListener() {
                    @Override
                    public void onTagClick(String content) {
                        processTagClick(content);
                    }
                }));

            }

            ll_like_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCommentAdapter != null) {
                        onCommentAdapter.onLikeClick((Comment) data);
                    }
                }
            });
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

    public interface OnCommentAdapter{
        void onLikeClick(Comment comment);
    }

}
