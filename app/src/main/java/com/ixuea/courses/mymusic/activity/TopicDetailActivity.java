package com.ixuea.courses.mymusic.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.FeedAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Feed;
import com.ixuea.courses.mymusic.domain.Topic;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ImageUtil;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TopicDetailActivity extends BaseTitleActivity {

    private String title;
    private String id;
    //private FeedAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private RecyclerView rv;
    private TextView tv_title;
    private TextView tv_count;
    private ListResponse.Meta meta;
    private ImageView iv_icon;
    private TextView tv_description;
    private FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

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

        id = getIntent().getStringExtra(Consts.ID);
        title = getIntent().getStringExtra(Consts.TITLE);

        if (StringUtils.isNotEmpty(id)) {
            //如果Id，不为空，就通过Id查询
            fetchDataById(id);
        } else if (StringUtils.isNotEmpty(title)) {
            //通过Title查询，主要是用在话题使用
            fetchDataByTitle(title);
        } else {
            finish();
        }

        adapter = new FeedAdapter(getActivity(), R.layout.item_feed);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {

            }
        });
        adapterWrapper = new LRecyclerViewAdapter(adapter);
        adapterWrapper.addHeaderView(createHeaderView());
        rv.setAdapter(adapterWrapper);

        tv_title.setText(title);
    }

    private void fetchDataByTitle(String title) {
        Api.getInstance().topicDetailByTitle(title).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Topic>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<Topic> data) {
                        super.onSucceeded(data);
                        nextDetail(data.getData());
                    }
                });
    }

    private void nextDetail(Topic data) {
        ImageUtil.show(getActivity(),iv_icon,data.getBanner());
        if (StringUtils.isNotBlank(data.getDescription())) {
            tv_description.setText(data.getDescription());
        }
        loadMore();
    }


    private void fetchDataById(String id) {
        Api.getInstance().topicDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Topic>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<Topic> data) {
                        super.onSucceeded(data);
                        nextDetail(data.getData());
                    }
                });
    }

    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_topic_detail, (ViewGroup) rv.getParent(), false);
        iv_icon = top.findViewById(R.id.iv_icon);
        tv_title = top.findViewById(R.id.tv_title);
        tv_count = top.findViewById(R.id.tv_count);
        tv_description = top.findViewById(R.id.tv_description);

        return top;
    }

    private void loadMore() {
        //Api.getInstance().feedsByTopic(title)
        //        .subscribeOn(Schedulers.io())
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .subscribe(new HttpListener<ListResponse<Feed>>(getActivity()) {
        //            @Override
        //            public void onSucceeded(ListResponse<Feed> data) {
        //                super.onSucceeded(data);
        //                meta = data.getMeta();
        //                next(data.getData());
        //            }
        //        });
    }

    private void next(java.util.List<Feed> data) {
        adapter.setData(data);
        tv_count.setText(getResources().getString(R.string.join_count,meta.getTotal_count()));
    }
}
