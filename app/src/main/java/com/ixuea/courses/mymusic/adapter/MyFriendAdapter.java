package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ixuea.courses.mymusic.fragment.FansFragment;
import com.ixuea.courses.mymusic.fragment.FriendFragment;


/**
 * 我的好友界面Adapter
 * Created by smile on 2018/5/26.
 */

public class MyFriendAdapter extends BaseFragmentPagerAdapter<Integer> {
    private static String[] titleNames = {"好友", "粉丝"};

    public MyFriendAdapter(Context context, FragmentManager fm) {
        super(context, fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return FriendFragment.newInstance();
        } else {
            return FansFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}