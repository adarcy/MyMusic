package com.ixuea.courses.mymusic.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.event.LogoutSuccessEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class SettingsActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
    }

    @OnClick(R.id.bt_logout)
    public void bt_logout() {
        sp.logout();
        AppContext.logout();

        //Tencent tencent = Tencent.createInstance(Consts.QQ_KEY, this.getApplicationContext());
        //tencent.logout(this);

        //清除第三方登陆信息
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (qq.isAuthValid()) {
            qq.removeAccount(true);
        }

        //发布退出登陆的信息，因为首页要根据登陆状态显示
        EventBus.getDefault().post(new LogoutSuccessEvent());

        finish();
    }
}
