package com.ixuea.courses.mymusic.domain.event;

/**
 * Created by smile on 2018/5/26.
 */

public class OnSearchKeyChangedEvent {
    private String content;

    public OnSearchKeyChangedEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
