package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ixuea.courses.mymusic.fragment.FMFragment;
import com.ixuea.courses.mymusic.fragment.FeedFragment;
import com.ixuea.courses.mymusic.fragment.RecommendFragment;


/**
 * Created by smile on 2018/5/26.
 */

public class MusicUIAdapter extends BaseFragmentPagerAdapter<Integer> {
    private static String[] titleNames = {"推荐", "朋友", "电台"};

    public MusicUIAdapter(Context context, FragmentManager fm) {
        super(context, fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return RecommendFragment.newInstance();
        } else if (position == 1) {
            return FeedFragment.newInstance();
        } else {
            return FMFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}