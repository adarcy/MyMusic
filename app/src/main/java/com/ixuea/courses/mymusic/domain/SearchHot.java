package com.ixuea.courses.mymusic.domain;

/**
 * 热门搜索
 * Created by smile on 02/03/2018.
 */

public class SearchHot extends Base{

    private String id;

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
}
