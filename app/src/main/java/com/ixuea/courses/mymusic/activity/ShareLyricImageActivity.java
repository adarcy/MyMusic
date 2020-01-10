package com.ixuea.courses.mymusic.activity;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.BitmapUtil;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ShareUtil;
import com.ixuea.courses.mymusic.util.ViewUtil;

import java.io.File;

public class ShareLyricImageActivity extends BaseTitleActivity {

    private RecyclerView rv;
    private String lyricText;
    private String banner;
    private ImageView iv_icon;
    private TextView tv_lyric;
    private LinearLayout lyric_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_lyric_image);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
        lyric_container = findViewById(R.id.lyric_container);
        iv_icon = findViewById(R.id.iv_icon);
        tv_lyric = findViewById(R.id.tv_lyric);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        lyricText = getIntent().getStringExtra(Consts.DATA);
        banner = getIntent().getStringExtra(Consts.URL);

        ImageUtil.show(getActivity(),iv_icon,banner);
        tv_lyric.setText(lyricText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                shareImage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
        Bitmap bitmap= ViewUtil.createBitmap(lyric_container);

        //一定要sd卡目陆，不然其他的应用不能读取

        File destFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/MyMusic"),"share.jpg");
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        BitmapUtil.saveToFile(bitmap,destFile);
        ShareUtil.shareImage(getActivity(),destFile.getAbsolutePath());
    }
}
