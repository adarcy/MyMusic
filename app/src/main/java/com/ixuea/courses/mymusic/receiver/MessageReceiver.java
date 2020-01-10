package com.ixuea.courses.mymusic.receiver;

import android.content.Context;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.util.LogUtil;
import com.ixuea.courses.mymusic.util.NotificationUtil;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 当您的应用处于后台运行或者和融云服务器 disconnect() 的时候，
 * 如果收到消息，融云 SDK 会以通知形式提醒您。
 * 所以您还需要自定义一个继承融云 PushMessageReceiver 的广播接收器，用来接收提醒通知
 * Created by smile on 2018/6/18.
 */

public class MessageReceiver extends PushMessageReceiver {
    private static final String TAG = "MessageReceiver";

    @Override
    public boolean onNotificationMessageArrived(final Context context, final PushNotificationMessage message) {
        LogUtil.d(TAG,"onNotificationMessageArrived:"+message.toString());
        AppContext.getImClient().getUnreadCount(Conversation.ConversationType.PRIVATE, message.getSenderId(), new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                NotificationUtil.showMessageNotification(context,message.getSenderId(),message.getPushContent(),integer);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

        return true; // 返回 false, 会弹出融云 SDK 默认通知; 返回 true, 融云 SDK 不会弹通知, 通知需要由您自定义。
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
        //LogUtil.d(TAG,"onNotificationMessageClicked:"+message.toString());
        return true; // 返回 false, 会走融云 SDK 默认处理逻辑, 即点击该通知会打开会话列表或会话界面; 返回 true, 则由您自定义处理逻辑。
    }

}
