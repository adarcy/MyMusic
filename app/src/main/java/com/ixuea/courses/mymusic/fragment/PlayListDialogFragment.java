package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.PlayListAdapter;
import com.ixuea.courses.mymusic.domain.Song;

import java.util.List;


/**
 * Created by smile on 2018/5/31.
 */

public class PlayListDialogFragment extends BottomSheetDialogFragment {
    private LRecyclerView rv;
    private PlayListAdapter adapter;
    private LinearLayout ll_loop_model_container;
    private TextView tv_play_all;
    private TextView tv_count;
    private Button bt_collection;
    private ImageView iv_delete_all;
    private List<Song> data;
    private BaseRecyclerViewAdapter.OnItemClickListener onItemClickListener;
    private PlayListAdapter.OnRemoveClickListener onRemoveClickListener;
    private Song currentSong;
    private LRecyclerViewAdapter adapterWrapper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_play_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv= view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

        adapter = new PlayListAdapter(getActivity(),R.layout.item_play_list);
        adapter.setCurrentSong(currentSong);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                onItemClickListener.onItemClick(holder,position);
            }
        });
        adapter.setOnRemoveClickListener(onRemoveClickListener);

        adapterWrapper = new LRecyclerViewAdapter(adapter);
        adapterWrapper.addHeaderView(createHeaderView());
        rv.setAdapter(adapterWrapper);

        rv.setPullRefreshEnabled(false);
        rv.setLoadMoreEnabled(false);

        adapter.setData(data);
    }

    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_play_list, (ViewGroup) rv.getParent(), false);
        ll_loop_model_container = top.findViewById(R.id.ll_loop_model_container);
        tv_play_all = top.findViewById(R.id.tv_play_all);
        bt_collection = top.findViewById(R.id.bt_collection);
        iv_delete_all = top.findViewById(R.id.iv_delete_all);
        tv_count = top.findViewById(R.id.tv_count);

        return top;
    }

    public void setData(List<Song> data) {
        this.data=data;
        if (adapter!=null) {
            adapter.setData(data);
        }
    }

    public void removeData(int index) {
        if (adapter!=null) {
            adapter.removeData(index);
        }
    }

    public void setOnRemoveClickListener(PlayListAdapter.OnRemoveClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
        if (adapter!=null) {
            adapter.setCurrentSong(currentSong);
        }
    }
    public void setOnItemClickListener(BaseRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}
