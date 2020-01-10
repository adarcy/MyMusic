package com.ixuea.courses.mymusic.domain.event;

import io.rong.imlib.model.Message;

/**
 * Created by smile on 2018/6/8.
 */

public class OnMessageEvent {
    private Message message;

    public OnMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
