package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ixuea.courses.mymusic.R;

/**
 * 歌单分组更多，有创建歌单，歌单管理
 * Created by smile on 2018/5/31.
 */

public class ListGroupMoreDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private OnListGroupMoreListener onListGroupMoreListener;
    private LinearLayout ll_create_list;
    private LinearLayout ll_list_manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_list_group_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ll_create_list = view.findViewById(R.id.ll_create_list);
        ll_list_manager = view.findViewById(R.id.ll_list_manager);

        initData();
        initListener();

    }

    private void initData() {
    }

    private void initListener() {
        ll_list_manager.setOnClickListener(this);
        ll_create_list.setOnClickListener(this);
    }

    public static void show(FragmentManager fragmentManager, OnListGroupMoreListener onMoreListener) {
        ListGroupMoreDialogFragment songMoreDialogFragment = new ListGroupMoreDialogFragment();
        songMoreDialogFragment.setListener(onMoreListener);
        songMoreDialogFragment.show(fragmentManager, "ListGroupMoreDialogFragment");
    }

    private void setListener(OnListGroupMoreListener onMoreListener) {
        this.onListGroupMoreListener = onMoreListener;
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.ll_create_list:
                onListGroupMoreListener.onCreateList();
                break;

            case R.id.ll_list_manager:
                onListGroupMoreListener.onManagerList();
                break;
        }
    }

    public interface OnListGroupMoreListener {
        void onCreateList();

        void onManagerList();
    }
}
