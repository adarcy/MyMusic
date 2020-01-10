package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;

/**
 * Created by smile on 2018/6/22.
 */

public class FMFragment extends BaseCommonFragment {

    public static FMFragment newInstance() {
        
        Bundle args = new Bundle();

        FMFragment fragment = new FMFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fm,null);
    }
}
