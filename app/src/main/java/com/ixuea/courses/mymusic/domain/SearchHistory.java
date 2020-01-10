package com.ixuea.courses.mymusic.domain;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by smile on 02/03/2018.
 */

public class SearchHistory extends Base{
    /**
     * 主键，使用当前Id的值
     */
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    /**
     * 标题
     */
    @NotNull
    private String content;

    /**
     * 创建时间
     */
    private long created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
