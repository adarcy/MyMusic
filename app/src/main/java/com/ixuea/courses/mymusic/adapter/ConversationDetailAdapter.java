package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.listener.OnTagClickListener;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.manager.impl.UserManagerImpl;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.TagUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * 会话详情adapter
 * Created by smile on 2018/5/26.
 */

public class ConversationDetailAdapter extends BaseRecyclerViewAdapter<Message,ConversationDetailAdapter.BaseViewHolder> {

    private static final int TYPE_TEXT_LEFT = 0;
    private static final int TYPE_TEXT_RIGHT = 10;
    private static final int TYPE_IMAGE_LEFT = 20;
    private static final int TYPE_IMAGE_RIGHT = 30;
    private static final long DEFAULT_TIME_SPLIT = 10*60*1000;
    private final UserManager userManager;

    public ConversationDetailAdapter(Context context) {
        super(context);
        userManager = UserManagerImpl.getInstance(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里要区分是我发送的，还是其他人发送的
        //我发送的显示在右边，其他人发送的显示在左边
        //他们的布局都一样只是方向不一样，所以可以用同一个ViewHolder
        switch (viewType) {
            case TYPE_IMAGE_LEFT:
                return new ImageViewHolder(getInflater().inflate(R.layout.item_chat_image_left, parent, false));
            case TYPE_IMAGE_RIGHT:
                return new ImageViewHolder(getInflater().inflate(R.layout.item_chat_image_right, parent, false));
            case TYPE_TEXT_LEFT:
                return new TextViewHolder(getInflater().inflate(R.layout.item_chat_text_left, parent, false));
                default:
                    return new TextViewHolder(getInflater().inflate(R.layout.item_chat_text_right, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bindData(getData(position),position);
    }

    @Override
    public int getItemViewType(int position) {
        Message data = getData(position);
        Message.MessageDirection messageDirection = data.getMessageDirection();
        MessageContent messageContent = data.getContent();

        if (messageContent instanceof ImageMessage) {
            //图片消息
            return messageDirection == Message.MessageDirection.SEND ? TYPE_IMAGE_RIGHT:TYPE_IMAGE_LEFT;
        }
        //TODO 其他消息可以继续在这里扩展

        //文本消息
        //我发送的消息在右边
        return messageDirection== Message.MessageDirection.SEND ? TYPE_TEXT_RIGHT:TYPE_TEXT_LEFT;
    }


    private long getLastMessageTime(int position) {
        if (position == 0) {
            return 0;
        }
        return getData(position-1).getSentTime();
    }

    abstract class BaseViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private final TextView tv_time;

        public BaseViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) findViewById(R.id.tv_time);
        }

        public void bindData(Message data, int position){
            //如果两条消息事件之间相差10分钟，就显示时间
            //这个规则，可以按照自己的业务来
            long time = data.getSentTime();
            long lastTime =getLastMessageTime(position);

            if (time - lastTime > DEFAULT_TIME_SPLIT) {
                tv_time.setText(TimeUtil.toLocalDate(time));
                tv_time.setVisibility(View.VISIBLE);
            } else {
                tv_time.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 聊天消息公共ViewHolder，比如头像
     */
    abstract class BaseChatViewHolder extends ConversationDetailAdapter.BaseViewHolder {

        private ImageView iv_avatar;

        public BaseChatViewHolder(View itemView) {
            super(itemView);
            iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        }

        public void bindData(Message data, int position){
            super.bindData(data,position);
            //这显示的信息，就显示发送人的信息就行了
            userManager.getUser(data.getSenderUserId(), new UserManager.OnUserListener() {
                @Override
                public void onUser(User user) {
                    ImageUtil.showCircle(context, iv_avatar, user.getAvatar());
                }
            });
        }
    }

    /**
     * 文本消息
     */
    private class TextViewHolder extends ConversationDetailAdapter.BaseChatViewHolder {

        private TextView tv_content;

        public TextViewHolder(View itemView) {
            super(itemView);
            tv_content = (TextView) findViewById(R.id.tv_content);
        }

        @Override
        public void bindData(final Message data, int position) {
            super.bindData(data, position);
            //设置后才可以点击
            tv_content.setMovementMethod(LinkMovementMethod.getInstance());

            //链接颜色
            tv_content.setLinkTextColor(context.getResources().getColor(R.color.text_Highlight));
            //tv_content.setHighlightColor(context.getResources().getColor(R.color.text_Highlight));
            tv_content.setText(TagUtil.process(context, ((TextMessage)data.getContent()).getContent(), new OnTagClickListener() {
                @Override
                public void onTagClick(String content) {
                    //processTagClick(content);
                }
            }));
        }
    }

    /**
     * 图片消息
     */
    private class ImageViewHolder extends ConversationDetailAdapter.BaseChatViewHolder {

        private ImageView iv_content;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_content = (ImageView) findViewById(R.id.iv_content);
        }

        @Override
        public void bindData(final Message data, int position) {
            super.bindData(data, position);
            ImageMessage imageMessage= ((ImageMessage)data.getContent());
            if (imageMessage.getRemoteUri() != null) {
                ImageUtil.showImage(context, iv_content, imageMessage.getRemoteUri().toString());
            } else if (imageMessage.getLocalUri() != null) {
                //刚发送时，只有本地地址
                ImageUtil.showImage(context,iv_content,imageMessage.getLocalUri().toString());
            }
            else {
                ImageUtil.showImage(context, iv_content, imageMessage.getThumUri().toString());
            }

        }
    }

}
