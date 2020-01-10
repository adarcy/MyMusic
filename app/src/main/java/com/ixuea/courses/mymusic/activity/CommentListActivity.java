package com.ixuea.courses.mymusic.activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.CommentAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Comment;
import com.ixuea.courses.mymusic.domain.event.FriendSelectedEvent;
import com.ixuea.courses.mymusic.domain.event.TopicSelectedEvent;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.fragment.CommentMoreDialogFragment;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.KeyboardUtil;
import com.ixuea.courses.mymusic.util.TagUtil;
import com.ixuea.courses.mymusic.util.ToastUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommentListActivity extends BaseTitleActivity implements View.OnClickListener, TextWatcher, CommentAdapter.OnCommentAdapter, OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = "TAG";
    private LRecyclerView rv;
    private CommentAdapter adapter;
    private String listId;
    private ListResponse.Meta pageMate;
    private LRecyclerViewAdapter adapterWrapper;
    private Button bt_send;
    private EditText et_content;
    private String parentId;
    private int touchSlop;
    private int lastContentLength;
    private int style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
        EventBus.getDefault().register(this);

        bt_send = findViewById(R.id.bt_send);
        et_content = findViewById(R.id.et_content);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getApplicationContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        listId = getIntent().getStringExtra(Consts.LIST_ID);
        style = getIntent().getIntExtra(Consts.STYLE, 0);

        adapter = new CommentAdapter(getActivity());
        adapter.setOnCommentAdapter(this);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                Object data = adapter.getData(position);
                if (data instanceof Comment) {
                    showCommentMoreDialog((Comment) data);
                }
            }
        });

        adapterWrapper = new LRecyclerViewAdapter(adapter);
        rv.setAdapter(adapterWrapper);

        fetchData();
    }

    private void showCommentMoreDialog(final Comment data) {
        CommentMoreDialogFragment dialogFragment = new CommentMoreDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                processOnClick(which, data);
            }
        });
    }

    private void processOnClick(int which, Comment data) {
        switch (which) {
            case 0:
                //回复评论
                parentId = data.getId();
                et_content.setHint(getResources().getString(R.string.reply_comment, data.getUser().getNickname()));
                break;
            case 1:
                //TODO 分享评论
                break;
            case 2:
                //TODO 复制评论
                break;
            case 3:
                //TODO 举报评论
                break;
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        //rv.addOnScrollListener(mOnScrollListener);
        rv.setOnRefreshListener(this);
        rv.setOnLoadMoreListener(this);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 100) {
                    //y轴滚动方向大于100
                    if (StringUtils.isEmpty(et_content.getText().toString())) {
                        //并且还没有输入内容，才清除回复
                        clearReplyComment();
                    }

                }
            }
        });
        et_content.addTextChangedListener(this);
        bt_send.setOnClickListener(this);
    }

    private void clearReplyComment() {
        parentId = null;
        et_content.setHint(R.string.hint_comment);
    }

    private void fetchData() {
        final ArrayList<Object> objects = new ArrayList<>();
        final HashMap<String, String> querys = getQuerys();
        querys.put(Consts.ORDER, String.valueOf(Consts.ORDER_HOT));

        if (StringUtils.isNotBlank(listId)) {
            querys.put(Consts.LIST_ID, listId);
        }

        objects.add("精彩评论");

        Api.getInstance().comments(querys)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<Comment>>(getActivity()) {
                    @Override
                    public void onSucceeded(ListResponse<Comment> data) {
                        super.onSucceeded(data);

                        objects.addAll(data.getData());
                        adapter.setData(objects);

                        adapter.addData("最新评论");
                        loadMore();
                    }
                });
    }

    @NonNull
    private HashMap<String, String> getQuerys() {
        final HashMap<String, String> querys = new HashMap<>();
        if (StringUtils.isNotBlank(listId)) {
            querys.put(Consts.LIST_ID, listId);
        }

        return querys;
    }

    private void loadMore() {
        final ArrayList<Object> objects = new ArrayList<>();

        final HashMap<String, String> querys = getQuerys();
        if (StringUtils.isNotBlank(listId)) {
            querys.put(Consts.LIST_ID, listId);
        }

        querys.put(Consts.PAGE, String.valueOf(ListResponse.Meta.nextPage(pageMate)));

        //最新评论
        Api.getInstance().comments(querys)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<Comment>>(getActivity()) {
                    @Override
                    public void onSucceeded(ListResponse<Comment> data) {
                        super.onSucceeded(data);
                        objects.addAll(data.getData());
                        adapter.addData(objects);
                        pageMate = data.getMeta();
                        rv.refreshComplete(Consts.DEFAULT_PAGE_SIZE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                sendComment();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void friendSelectedEvent(FriendSelectedEvent event) {
        //原来的内容加选择的输入
        //这里的@用户使用的昵称，如果有相同昵称的就会出问题
        //所以要实现好这个功能，最好附加上用户ID，但这会增加更多的代码
        //我们会在相应的课程提供整套解决方案
        //这里只是简单实现
        String source = et_content.getText().toString();
        setText(source + event.getUser().getNickname() + " ");

        //将光标移动最后
        et_content.setSelection(et_content.getText().toString().length());
    }

    private void setText(String s) {
        //高亮Tag
        et_content.setText(TagUtil.processHighlight(getActivity(), s));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void topicSelectedEvent(TopicSelectedEvent event) {
        String source = et_content.getText().toString();

        //这里也只是简单实现，没有保存话题Id
        //不过话题这样的，就不应该重复
        //为什么话题最好用#包裹，因为可能存在空格
        setText(source + event.getTopic().getTitle() + "# ");

        //将光标移动最后
        et_content.setSelection(et_content.getText().toString().length());
    }

    private void sendComment() {
        String content = et_content.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtil.showSortToast(getActivity(), getString(R.string.enter_comment_content));
            return;
        }

        Comment comment = new Comment();
        comment.setParent_id(parentId);
        comment.setContent(content);
        comment.setSheet_id(listId);
        comment.setStyle(style);

        Api.getInstance().createComment(comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Comment>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<Comment> data) {
                        super.onSucceeded(data);
                        ToastUtil.showSortToast(getActivity(), getString(R.string.comment_create_susscess));
                        onRefresh();

                        ////重新设置最热评论，然后在拉去第一页评论
                        ////当然也可以手动将发送的评论插入到adapter中
                        ////拉去第一页
                        //pageMate.setCurrent_page(1);
                        //loadMore();

                        //请空输入框
                        et_content.setText("");
                        clearReplyComment();
                        //关闭键盘
                        KeyboardUtil.hideKeyboard(getActivity());
                    }
                });


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int currentLength = s.length();
        if (currentLength > lastContentLength) {
            //新增内容，如果不判断，用户删除到@,#符号就会跳转
            String string = s.toString();
            String lastChar = string.substring(s.length() - 1);
            if (Consts.MENTION.equals(lastChar)) {
                //输入了@符号，跳转到用户的好友列表
                startActivity(SelectFriendActivity.class);
            } else if (Consts.HAST_TAG.equals(lastChar)) {
                //输入了#符号，跳转到话题列表
                startActivity(SelectTopicActivity.class);
            }
        }

        lastContentLength = s.length();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onLikeClick(final Comment comment) {
        if (comment.isLiked()) {
            //已经点赞
            Api.getInstance().unlike(comment.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpListener<DetailResponse<Comment>>(getActivity()) {
                        @Override
                        public void onSucceeded(DetailResponse<Comment> data) {
                            super.onSucceeded(data);
                            //可以调用接口，也可以在本地加减
                            comment.setLikes_count(comment.getLikes_count() - 1);
                            comment.setLike_id(null);
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else {
            Api.getInstance().like(comment.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpListener<DetailResponse<Comment>>(getActivity()) {
                        @Override
                        public void onSucceeded(DetailResponse<Comment> data) {
                            super.onSucceeded(data);
                            comment.setLikes_count(comment.getLikes_count() + 1);
                            comment.setLike_id("1");
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onRefresh() {
        pageMate.setCurrent_page(0);
        fetchData();
    }

    @Override
    public void onLoadMore() {
        if (pageMate==null||pageMate.getCurrent_page() < pageMate.getTotal_pages()) {
            loadMore();
        } else {
            rv.setNoMore(true);
        }
    }
}
