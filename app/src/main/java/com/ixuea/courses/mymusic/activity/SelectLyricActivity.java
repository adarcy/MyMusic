package com.ixuea.courses.mymusic.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.SelectLyricAdapter;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ShareUtil;
import com.ixuea.courses.mymusic.util.ToastUtil;

import java.util.ArrayList;

public class SelectLyricActivity extends BaseTitleActivity implements View.OnClickListener {

    private RecyclerView rv;
    private TextView tv_share_text_lyric;
    private TextView tv_share_image_lyric;

    private SelectLyricAdapter adapter;

    private ArrayList<Line> lyricLines;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lyric);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        rv=findViewById(R.id.rv);
        tv_share_text_lyric=findViewById(R.id.tv_share_text_lyric);
        tv_share_image_lyric=findViewById(R.id.tv_share_image_lyric);

        rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);


    }

    @Override
    protected void initDatas() {
        super.initDatas();
        adapter = new SelectLyricAdapter(getActivity(),R.layout.item_select_lyric);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                if (adapter.isSelected(position)) {
                    //当前选中，切换为没选中
                    adapter.setSelected(position,false);
                } else {
                    //当前没选中，切换为选中
                    adapter.setSelected(position,true);
                }
            }
        });

        rv.setAdapter(adapter);

        lyricLines = (ArrayList<Line>) getIntent().getSerializableExtra(Consts.DATA);
        index = getIntent().getIntExtra(Consts.ID,0);

        adapter.setData(lyricLines);
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_share_text_lyric.setOnClickListener(this);
        tv_share_image_lyric.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share_text_lyric:
                shareTextLyric();
                break;
            case R.id.tv_share_image_lyric:
                shareImageLyric();
                break;
        }
    }

    private void shareImageLyric() {
        //将歌词，以及图片传递过去
        StringBuilder sb = new StringBuilder();
        int[] selectedIndex = adapter.getSelectedIndex();
        for (int i = 0; i < selectedIndex.length; i++) {
            if (selectedIndex[i] == 1) {
                //当前选中了
                sb.append(adapter.getData(i).getLineLyrics());
                sb.append("\n");
            }
        }

        //如果有换行，需要去掉
        int lastIndexOf = sb.lastIndexOf("\n");
        if (lastIndexOf != -1) {
            sb.deleteCharAt(sb.length() - 1);
        } else {
            ToastUtil.showSortToast(getActivity(),R.string.share_lyric_hint);
            return;
        }

        PlayListManager playListManager = MusicPlayerService.getPlayListManager(getApplicationContext());
        Song song = playListManager.getPlayData();

        Intent intent = new Intent(this, ShareLyricImageActivity.class);
        intent.putExtra(Consts.DATA,sb.toString());
        intent.putExtra(Consts.URL,song.getBanner());
        startActivity(intent);
    }

    private void shareTextLyric() {
        StringBuilder sb = new StringBuilder();
        sb.append("分享歌词：\n");
        int[] selectedIndex = adapter.getSelectedIndex();

        for (int i = 0; i < selectedIndex.length; i++) {
            if (selectedIndex[i] == 1) {
                //当前选中了
                sb.append(adapter.getData(i).getLineLyrics());
                sb.append("，");
            }
        }

        //如果有逗号，需要去掉，换成句号
        int lastIndexOf = sb.lastIndexOf("，");
        if (lastIndexOf != -1) {
            sb.deleteCharAt(sb.length() - 1);
        } else {
            ToastUtil.showSortToast(getActivity(),R.string.share_lyric_hint);
            return;
        }

        sb.append("。");

        //歌曲信息
        PlayListManager playListManager = MusicPlayerService.getPlayListManager(getApplicationContext());
        Song song = playListManager.getPlayData();
        sb.append(String.format(Consts.SHARE_TEXT_LYRIC_TEMPLATE,song.getArtist_name(),song.getAlbum_title(),song.getId()));

        //格式化Url
        String url= String.format(Consts.LYRIC_URL, song.getId());

        ShareUtil.shareText(getActivity(),sb.toString(),url);
    }
}
