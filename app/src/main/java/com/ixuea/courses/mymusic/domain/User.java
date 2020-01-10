package com.ixuea.courses.mymusic.domain;

import android.text.TextUtils;

/**
 * Created by smile on 02/03/2018.
 */

public class User extends Base{
    /**
     * 手机号
     */
    public static final int TYPE_PHONE = 0;

    /**
     * QQ登陆
     */
    public static final int TYPE_QQ = 10;

    /**
     * 微博登陆
     */
    public static final int TYPE_WEIBO = 20;

    /**
     * 用户Id
     */
    private String id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户描述
     */
    private String description;

    /**
     * 第三方OpenId
     */
    private String open_id;

    /**
     * 用户的手机号
     */
    private String phone;

    /**
     * 用户的密码,登陆，注册向服务端传递
     */
    private String password;

    /**
     * 类型，只有登陆，注册向服务端传递，表示是什么样的登陆，或者
     */
    private int type;

    /**
     * 关注我的人
     */
    private int followers_count;

    /**
     * 我关注的人
     */
    private int followings_count;

    /**
     * 是否关注，1：关注
     */
    private int following;

    public int getFollowing() {
        return following;
    }

    public boolean isFollowing() {
        return following==1;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFollowings_count() {
        return followings_count;
    }

    public void setFollowings_count(int followings_count) {
        this.followings_count = followings_count;
    }

    public int getType() {
        return type;
    }

    public User setType(int type) {
        this.type = type;
        return this;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getNickname() {
        return nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        if (TextUtils.isEmpty(description)) {
            return "这个人很懒，没有填写个人介绍！";
        }
        return description;
    }

    public User setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
