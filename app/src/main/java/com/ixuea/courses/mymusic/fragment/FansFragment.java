package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.FriendAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 粉丝
 * Created by smile on 02/03/2018.
 */

public class FansFragment extends BaseCommonFragment {
    private static final long ANIMATION_DURATION = 500;
    RecyclerView rv;
    private FriendAdapter adapter;

    public static FansFragment newInstance() {

        Bundle args = new Bundle();
        FansFragment fragment = new FansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

    }


    @Override
    protected void initDatas() {
        super.initDatas();

        adapter = new FriendAdapter(getActivity(),R.layout.item_friend);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                User data = adapter.getData(position);
                startActivityExtraId(UserDetailActivity.class, data.getId());
            }
        });

        rv.setAdapter(adapter);

        fetchData();
    }


    private void fetchData() {
        Api.getInstance().myFans(sp.getUserId(),null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<User>>(getMainActivity()) {
                    @Override
                    public void onSucceeded(ListResponse<User> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    private void next(List<User> data) {
        adapter.setData(data);
    }


    @Override
    protected void initListener() {
        super.initListener();
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend, null);
    }


}
