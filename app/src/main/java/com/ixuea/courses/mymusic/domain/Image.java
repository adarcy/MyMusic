package com.ixuea.courses.mymusic.domain;

import java.io.Serializable;

/**
 *
 * 图片
 * Created by smile on 2018/5/29.
 */

public class Image implements Serializable {

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
