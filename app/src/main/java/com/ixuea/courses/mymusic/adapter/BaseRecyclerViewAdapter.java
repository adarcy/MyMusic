package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smile on 25/04/2018.
 */

public abstract class BaseRecyclerViewAdapter<D, VH extends BaseRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected final Context context;
    private final LayoutInflater inflater;
    private List<D> datas = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public List<D> getDatas() {
        return datas;
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder,position);
                }
            });

        }

        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    return onItemLongClickListener.onItemLongClick(holder,position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void addData(int index,List<D> data) {
        this.datas.addAll(index,data);
        //notifyItemRangeChanged(data.size() - 1, data.size());
        notifyDataSetChanged();
    }

    public void addData(List<D> data) {
        this.datas.addAll(data);
        //notifyItemRangeChanged(data.size() - 1, data.size());
        notifyDataSetChanged();
    }

    public void setData(List<D> data) {
        this.datas.clear();
        this.datas.addAll(data);
        //notifyItemRangeChanged(0, data.size());
        notifyDataSetChanged();
    }

    public void addData(D data) {
        datas.add(data);
        notifyItemInserted(datas.size()-1);
        //notifyDataSetChanged();
    }

    public void addData(int index, D data) {
        datas.add(index, data);
        notifyItemInserted(index);
    }

    public void removeData(int index) {
        datas.remove(index);
        //notifyItemRemoved(index);
        notifyDataSetChanged();
    }

    public void removeData(D d) {
        datas.remove(d);
        notifyDataSetChanged();
    }

    public D getData(int position) {
        return datas.get(position);
    }

    public int setSpanSizeLookup(int position) {
        return 1;
    }

    public void clearData() {
        datas.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(BaseRecyclerViewAdapter.ViewHolder holder, int position);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            //TODO 可以在这里加入butterknife这样的框架。
        }

        @Nullable
        public final <T extends View> T findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }


    }
}
