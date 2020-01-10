package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smile on 08/03/2018.
 */

public abstract class BaseFragmentPagerAdapter<T> extends FragmentPagerAdapter {
    protected final Context context;
    protected final List<T> datas = new ArrayList<T>();

    public BaseFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    public T getData(int position) {
        return datas.get(position);
    }

    public void setDatas(List<T> data) {
        if (data != null && data.size() > 0) {
            datas.clear();
            datas.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addDatas(List<T> data) {
        if (data != null && data.size() > 0) {
            datas.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}
