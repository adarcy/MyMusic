package com.ixuea.courses.mymusic.domain;

/**
 * 话题
 * Created by smile on 02/03/2018.
 */

public class Topic extends Base{
    private String id;
    private String title;
    private String banner;
    private String description;

    /**
     * 参与人数
     */
    private int joins_count;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getJoins_count() {
        return joins_count;
    }

    public void setJoins_count(int joins_count) {
        this.joins_count = joins_count;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
