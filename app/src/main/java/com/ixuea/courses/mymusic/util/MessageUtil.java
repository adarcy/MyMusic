package com.ixuea.courses.mymusic.util;


import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * Created by smile on 2018/6/19.
 */

public class MessageUtil {
    /**
     * 获取对方的Id
     * @param message
     * @return
     */
    public static String getTargetId(Message message) {
        if (message.getMessageDirection() == io.rong.imlib.model.Message.MessageDirection.SEND) {
            //发送，显示targetId
            return message.getTargetId();
        } else {
            return message.getSenderUserId();
        }
    }

    /**
     * 将不同的消息转为可文字描述的消息
     *
     * <p>用在通知栏，会话列表</p>
     *
     * @param messageContent
     * @return
     */
    public static String getContent(MessageContent messageContent) {
        if (messageContent instanceof TextMessage) {
            return ((TextMessage) messageContent).getContent();
        } else if (messageContent instanceof ImageMessage) {
            return "[图片]";
        }
        return "";
    }
}
