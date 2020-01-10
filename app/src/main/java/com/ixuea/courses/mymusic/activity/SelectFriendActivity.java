package com.ixuea.courses.mymusic.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.FriendAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.event.FriendSelectedEvent;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SelectFriendActivity extends BaseTitleActivity {

    private RecyclerView rv;
    private FriendAdapter adapter;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
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

        adapter = new FriendAdapter(getActivity(),R.layout.item_friend);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                User data = adapter.getData(position);
                EventBus.getDefault().post(new FriendSelectedEvent(data));
                finish();
            }
        });

        rv.setAdapter(adapter);

        fetchData();
    }

    private void fetchData() {
        Api.getInstance().myFriends(sp.getUserId(),nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<User>>(getActivity()) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_view);
        final SearchView searchView =
                (SearchView) searchItem.getActionView();
        //可以在这里配置SearchView

        //设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchTextChanged(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onSearchTextChanged(newText);
                return true;
            }
        });

        //是否进入界面就打开搜索栏，false为默认打开，默认为true
        searchView.setIconified(false);

        searchView.setQueryHint(getString(R.string.enter_nickname));
        return true;
    }

    private void onSearchTextChanged(String query) {
        nickname=query;
        fetchData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_view) {
            return true;
        }

        //调用父类的方法，因为父类可以实现通过的按钮事件，比如：返回
        return super.onOptionsItemSelected(item);
    }
}
