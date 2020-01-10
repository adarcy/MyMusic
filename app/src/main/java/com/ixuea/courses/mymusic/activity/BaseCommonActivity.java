package com.ixuea.courses.mymusic.activity;


import com.ixuea.courses.mymusic.manager.FloatingLayoutManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.OrmUtil;
import com.ixuea.courses.mymusic.util.ServiceUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;

import butterknife.ButterKnife;

/**
 * Created by smile on 02/03/2018.
 */

public class BaseCommonActivity extends BaseActivity {

    protected SharedPreferencesUtil sp;
    protected OrmUtil orm;
    private FloatingLayoutManager floatingLayoutManager;


    @Override
    protected void initViews() {
        super.initViews();
        ButterKnife.bind(this);


        sp = SharedPreferencesUtil.getInstance(getApplicationContext());
        orm = OrmUtil.getInstance(getApplicationContext());
        floatingLayoutManager = MusicPlayerService.getFloatingLayoutManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ServiceUtil.isBackgroundRunning(getApplicationContext())) {
            //如果当前程序在前台，就尝试隐藏桌面歌词
            floatingLayoutManager.tryHide();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ServiceUtil.isBackgroundRunning(getApplicationContext())) {
            //如果当前程序在后台，就显示桌面歌词
            floatingLayoutManager.tryShow();
        }
    }
}
