package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.fragment.MusicRecordFragment;

public class MusicPlayerAdapter extends BaseFragmentPagerAdapter<Song> {
    public MusicPlayerAdapter(Context context, FragmentManager fm) {
        super(context, fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MusicRecordFragment.newInstance(getData(position));
    }
}
