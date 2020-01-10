package com.ixuea.courses.mymusic.domain;

/**
 * Created by smile on 02/03/2018.
 */

public class Session extends Base{
    private String id; //当前用户的user id
    private String token;
    /**
     * 聊天用的token
     */
    private String im_token;

    public String getIm_token() {
        return im_token;
    }

    public void setIm_token(String im_token) {
        this.im_token = im_token;
    }

    public String getToken() {
        return token;
    }

    public Session setToken(String token) {
        this.token = token;
        return this;
    }

    public String getId() {
        return id;
    }

    public Session setId(String id) {
        this.id = id;
        return this;
    }
}
