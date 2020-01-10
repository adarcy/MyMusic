package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by smile on 25/04/2018.
 */

public abstract class BaseQuickRecyclerViewAdapter<D> extends BaseRecyclerViewAdapter<D,BaseQuickRecyclerViewAdapter.ViewHolder> {
    private final int layoutId;

    public BaseQuickRecyclerViewAdapter(Context context, @LayoutRes int layoutId) {
        super(context);
        this.layoutId=layoutId;
    }

    @NonNull
    @Override
    public BaseQuickRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseQuickRecyclerViewAdapter.ViewHolder(getInflater().inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseQuickRecyclerViewAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        bindData(holder,position,getData(position));
    }

    protected abstract void bindData(ViewHolder holder, int position, D data);

    public class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        /**
         * 用来保存item view，使用SparseArray是因为有更好的性能
         * 后面的课程我们会分析他的源码，查看到底高效在哪里
         */
        private final SparseArray<View> itemViews = new SparseArray<>();

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder setText(@IdRes int id, CharSequence value) {
            TextView view = getView(id);
            view.setText(value);
            return this;
        }

        public ViewHolder setText(@IdRes int id, @StringRes int value) {
            TextView view = getView(id);
            view.setText(value);
            return this;
        }

        public ViewHolder setImageResource(@IdRes int id, int value) {
            ImageView view = getView(id);
            view.setImageResource(value);
            return this;
        }

        public ViewHolder setVisibility(@IdRes int id, int visibility) {
            View view = getView(id);
            view.setVisibility(visibility);
            return this;
        }

        //TODO 这里可以添加更多类型的方法

        public <T extends View> T getView(@IdRes int id) {
            View view = itemViews.get(id);
            if (view == null) {
                view = findViewById(id);
                itemViews.put(id, view);
            }
            return (T) view;
        }

        public void setTextColorRes(@IdRes int id, @ColorRes  int resId) {
            TextView view = getView(id);
            view.setTextColor(view.getResources().getColor(resId));
        }

        public void setOnClickListener(@IdRes int id, View.OnClickListener onClickListener) {
            View view = getView(id);
            view.setOnClickListener(onClickListener);
        }

        public void setMax(@IdRes int id, long size) {
            ProgressBar view = getView(id);
            view.setMax((int) size);
        }

        public void setProgress(@IdRes int id, long progress) {
            ProgressBar view = getView(id);
            view.setProgress((int) progress);
        }
    }
}
