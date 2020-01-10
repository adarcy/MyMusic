package com.ixuea.courses.mymusic.domain.response;

/**
 * Created by smile on 02/03/2018.
 */

public class DetailResponse<T> extends BaseResponse{
    private T data;

    public T getData() {
        return data;
    }

    public DetailResponse setData(T data) {
        this.data = data;
        return this;
    }
}
