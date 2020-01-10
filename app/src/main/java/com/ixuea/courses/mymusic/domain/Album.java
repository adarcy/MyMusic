package com.ixuea.courses.mymusic.domain;

import java.util.List;

/**
 * 专辑
 * Created by smile on 2018/5/30.
 */

public class Album extends Base {

    private String id;

    /**
     * 专辑名称
     */
    private String title;


    /**
     * 封面
     */
    private String banner;


    /**
     * 发行日期
     */
    private String released_at;

    /**
     * 评论数
     */
    private long comments_count;

    /**
     * 收藏次数
     */
    private long collections_count;

    /**
     * 艺术家
     */
    private User artist;

    /**
     * 该专辑下面的歌曲
     */
    private List<Song> songs;

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

    public String getReleased_at() {
        return released_at;
    }

    public void setReleased_at(String released_at) {
        this.released_at = released_at;
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

    public User getArtist() {
        return artist;
    }

    public void setArtist(User artist) {
        this.artist = artist;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
