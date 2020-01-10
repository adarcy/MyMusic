package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ixuea.courses.mymusic.fragment.SearchSongResultFragment;
import com.ixuea.courses.mymusic.fragment.SearchVideoResultFragment;

/**
 * 搜索结果ViewPager
 * Created by smile on 2018/5/26.
 */

public class SearchResultAdapter extends BaseFragmentPagerAdapter<Integer> {
    private static String[] titleNames = {"单曲", "视频","歌手","专辑","歌单","主播电台","用户"};

    public SearchResultAdapter(Context context, FragmentManager fm) {
        super(context, fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return SearchSongResultFragment.newInstance();
        } else {
            //TODO 更多搜索
            return SearchVideoResultFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}