package com.ixuea.courses.mymusic.activity;

import android.app.SearchManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.ArrayAdapter;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.SearchHistoryAdapter;
import com.ixuea.courses.mymusic.adapter.SearchResultAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.SearchHistory;
import com.ixuea.courses.mymusic.domain.SearchHot;
import com.ixuea.courses.mymusic.domain.event.OnSearchKeyChangedEvent;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.KeyboardUtil;
import com.ixuea.courses.mymusic.util.LogUtil;
import com.nex3z.flowlayout.FlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends BaseMusicPlayerActivity {

    private static final String TAG = "TAG";
    private RelativeLayout rl_singer;
    private RecyclerView rv;
    private SearchHistoryAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private FlowLayout fl;
    private LayoutInflater inflater;
    private SearchResultAdapter searchResultAdapter;
    private ViewPager vp;
    private LinearLayout ll_search_result_container;
    private TabLayout tabs;
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;

    //这个类类别的依赖兼容性没处理好
    private ArrayAdapter<String> stringArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
        inflater = LayoutInflater.from(getActivity());

        tabs = findViewById(R.id.tabs);

        vp = findViewById(R.id.vp);
        vp.setOffscreenPageLimit(7);
        ll_search_result_container = findViewById(R.id.ll_search_result_container);

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
        //搜索历史
        adapter = new SearchHistoryAdapter(getActivity(), R.layout.item_search_history);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                SearchHistory data = adapter.getData(position);
                onSearchClick(data.getContent());
            }
        });
        adapter.setOnSearchHistoryListener(new SearchHistoryAdapter.OnSearchHistoryListener() {
            @Override
            public void onRemoveClick(int position, SearchHistory data) {
                adapter.removeData(position);
                orm.deleteSearchHistory(data);
            }
        });

        adapterWrapper = new LRecyclerViewAdapter(adapter);

        adapterWrapper.addHeaderView(createHeaderView());

        rv.setAdapter(adapterWrapper);

        fetchData();

        //显示header内容
        fetchHotSearch();

        //搜索结果展示ViwPager
        searchResultAdapter = new SearchResultAdapter(getActivity(),getSupportFragmentManager());
        vp.setAdapter(searchResultAdapter);
        tabs.setupWithViewPager(vp);

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(0);
        integers.add(1);
        integers.add(2);
        integers.add(3);
        integers.add(4);
        integers.add(5);
        integers.add(6);
        searchResultAdapter.setDatas(integers);
    }

    /**
     * 查询搜索历史
     */
    private void fetchData() {
        java.util.List<SearchHistory> searchHistory=orm.queryAllSearchHistory();
        adapter.setData(searchHistory);
    }

    private void fetchHotSearch() {
        Api.getInstance().searchHot().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<SearchHot>>(getActivity()) {
                    @Override
                    public void onSucceeded(final ListResponse<SearchHot> data) {
                        super.onSucceeded(data);
                        setHotData(data.getData());
                    }
                });
    }


    private void setHotData(java.util.List<SearchHot> data) {
        for (final SearchHot searchHot : data) {
            TextView tv = (TextView) inflater.inflate(R.layout.item_hot_search,
                    fl, false);
            tv.setText(searchHot.getContent());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchClick(searchHot.getContent());
                }
            });
            fl.addView(tv);
        }
    }

    private void onSearchClick(String content) {
        searchView.setQuery(content,true);
        //是否进入界面就打开搜索栏，false为默认打开，默认为true
        searchView.setIconified(false);

        KeyboardUtil.hideKeyboard(this);
    }

    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_search, (ViewGroup) rv.getParent(), false);
        rl_singer = top.findViewById(R.id.rl_singer);
        fl = top.findViewById(R.id.fl);

        return top;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_view);
        searchView = (SearchView) searchItem.getActionView();
        //可以在这里配置SearchView

        //设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //自定义搜索建议
                getSuggestionsData(newText);
                return true;
            }
        });


        //关闭监听器
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                changeNormalView();
                return false;
            }
        });

        //是否进入界面就打开搜索栏，false为默认打开，默认为true
        searchView.setIconified(false);

        searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        //默认要输入两个字符才显示提示，可以这样更改
        searchAutoComplete.setThreshold(1);

        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.d("suggestion onItemClick:"+position);
                performSearch(stringArrayAdapter.getItem(position));
            }
        });

        return true;
    }

    private void getSuggestionsData(String newText) {
        Log.d(TAG, "getSuggestionsData: "+newText);
        if (newText.length() > 0) {
            //showSuggestionWindow();
            fetchPrompt(newText);
        }
    }
    private void fetchPrompt(String content) {
        Api.getInstance().prompt(content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<SearchHot>>(getActivity()) {
                    @Override
                    public void onSucceeded(final ListResponse<SearchHot> data) {
                        super.onSucceeded(data);
                        setPromptData(data.getData());
                    }
                });
    }

    private void setPromptData(List<SearchHot> data) {
        if (data.size() > 0) {
            //像变换这个中操作，如果是Kotlin语言中就一句话的事
            ArrayList<String> strings = new ArrayList<>();
            for (SearchHot hot:data
                    ) {
                strings.add(hot.getContent());
            }
            stringArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_suggestion, R.id.tv_title, strings);
            searchAutoComplete.setAdapter(stringArrayAdapter);
        }
    }

    private void hideSuggestionWindow() {
    }

    private void performSearch(String data) {
        Log.d(TAG, "performSearch: "+data);

        //发布搜索Key
        EventBus.getDefault().post(new OnSearchKeyChangedEvent(data));

        //保存搜索
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setContent(data);
        searchHistory.setCreated_at(System.currentTimeMillis());
        orm.createOrUpdate(searchHistory);

        fetchData();

        changeSearchResultView();
    }

    private void changeSearchResultView() {
        ll_search_result_container.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
    }

    private void changeNormalView() {
        ll_search_result_container.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
    }
}
