package com.ixuea.courses.mymusic.domain.response;

/**
 * Created by smile on 03/03/2018.
 */

public class BaseResponse {
    /**
     * 状态码，有状态码，表示有出错
     */
    private Integer status;

    /**
     * 出错的提示信息，有可能没有值
     */
    private String message;

    public Integer getStatus() {
        return status;
    }

    public BaseResponse setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
