package com.ixuea.courses.mymusic.domain.response;

import java.util.List;

/**
 * Created by smile on 02/03/2018.
 */

public class ListResponse<T> extends BaseResponse{
    private List<T> data;

    private Meta meta;

    public List<T> getData() {
        return data;
    }

    public ListResponse setData(List<T> data) {
        this.data = data;
        return this;
    }

    public Meta getMeta() {
        return meta;
    }

    public ListResponse setMeta(Meta meta) {
        this.meta = meta;
        return this;
    }

    public static class Meta{
        private int current_page;
        private int total_pages;
        private int total_count;

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public Meta setCurrent_page(int current_page) {
            this.current_page = current_page;
            return this;
        }

        public int getTotal_pages() {
            return total_pages;
        }

        public Meta setTotal_pages(int total_pages) {
            this.total_pages = total_pages;
            return this;
        }

        /**
         * 开始页数是1
         * @param page
         * @return
         */
        public static int nextPage(Meta page) {
            if (page != null) {
                return page.getCurrent_page()+1;
            }
            return 1;
        }
    }
}
