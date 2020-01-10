package com.ixuea.courses.mymusic.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * 歌单
 * Created by smile on 2018/5/26.
 */

public class List  extends Base {
    /**
     * 歌单Id
     */
    private String id;

    /**
     * 歌单名称
     */
    private String title;

    /**
     * 歌单图片
     */
    private String banner;

    /**
     * 单点击次数
     */
    private long clicks_count;

    /**
     * 评论数
     */
    private long comments_count;

    /**
     * 收藏次数
     */
    private long collections_count;

    /**
     * 歌曲数量
     */
    private long songs_count;

    /**
     * 歌单描述
     */
    private String description;

    /**
     * 创建人
     */
    private User user;

    /**
     * 创建事件，ISO8601格式
     */
    private String created_at;


    /**
     * 该歌单下面的歌曲
     */
    private java.util.List<Song> songs;

    /**
     * 是否收藏，有值表示收藏，null表示没收藏
     */
    private String collection_id;

    public String getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(String collection_id) {
        this.collection_id = collection_id;
    }

    public long getSongs_count() {
        return songs_count;
    }

    public void setSongs_count(long songs_count) {
        this.songs_count = songs_count;
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

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public long getClicks_count() {
        return clicks_count;
    }

    public void setClicks_count(long clicks_count) {
        this.clicks_count = clicks_count;
    }

    public long getComments_count() {
        return comments_count;
    }

    public void setComments_count(long comments_count) {
        this.comments_count = comments_count;
    }

    public long getCollections_count() {
        return collections_count;
    }

    public void setCollections_count(long collections_count) {
        this.collections_count = collections_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public java.util.List<Song> getSongs() {
        return songs;
    }

    public void setSongs(java.util.List<Song> songs) {
        this.songs = songs;
    }

    public boolean isCollection() {
        return StringUtils.isNotBlank(collection_id);
    }
}
