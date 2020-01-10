package com.ixuea.courses.mymusic.domain;

import java.io.Serializable;

/**
 *
 * 歌曲中，服务器返回的接口的，对应的歌词
 * Created by smile on 2018/5/29.
 */

public class Lyric implements Serializable {

    /**
     * 歌词id
     */
    private String id;

    /**
     * 歌词类型，0:LRC，10:KSC
     */
    private int style;

    /**
     * 歌词内容
     */
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
