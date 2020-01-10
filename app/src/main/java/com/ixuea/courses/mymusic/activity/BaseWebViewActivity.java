package com.ixuea.courses.mymusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.Consts;

public class BaseWebViewActivity extends BaseTitleActivity {

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_web_view);
    }


    /**
     * 定义静态的启动方法，好处是用户只要看到声明，就知道该界面需要哪些参数
     * @param activity
     * @param title
     * @param url
     */
    public static void start(Activity activity, String title, String url) {
        Intent intent = new Intent(activity, BaseWebViewActivity.class);
        intent.putExtra(Consts.TITLE, title);
        intent.putExtra(Consts.URL, url);
        activity.startActivity(intent);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        wv=findViewById(R.id.wv);
        WebSettings webSettings = wv.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webSettings.setDomStorageEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        String title = getIntent().getStringExtra(Consts.TITLE);
        String url = getIntent().getStringExtra(Consts.URL);

        setTitle(title);

        if (!TextUtils.isEmpty(url)) {
            wv.loadUrl(url);
        }  else {
            finish();
        }
    }
}
