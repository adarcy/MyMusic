package com.ixuea.courses.mymusic.domain;

/**
 * Created by smile on 2018/6/5.
 */

public class MeUI {
    private String title;
    private java.util.List<List> list;

    public MeUI(String title, java.util.List<List> list) {
        this.title = title;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public void setList(java.util.List<List> list) {
        this.list = list;
    }
}
