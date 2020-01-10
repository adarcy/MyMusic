package com.ixuea.courses.mymusic.domain;

/**
 * 广告
 * Created by smile on 11/03/2018.
 */

public class Advertisement extends Base {
    /**
     * 图片地址
     */
    private String banner;

    /**
     * 点击跳转的页面
     */
    private String uri;

    /**
     * 广告标题
     */
    private String title;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
