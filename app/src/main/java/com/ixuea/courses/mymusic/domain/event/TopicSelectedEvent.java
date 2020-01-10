package com.ixuea.courses.mymusic.domain.event;

import com.ixuea.courses.mymusic.domain.Topic;

/**
 * Created by smile on 2018/5/26.
 */

public class TopicSelectedEvent {
    private Topic topic;

    public TopicSelectedEvent(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
