package com.ixuea.courses.mymusic.domain.param;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 创建动态
 * Created by smile on 2018/5/29.
 */

public class FeedParam implements Serializable {

    private String content;

    private String song_id;

    private String video_id;

    private List<String> images;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
