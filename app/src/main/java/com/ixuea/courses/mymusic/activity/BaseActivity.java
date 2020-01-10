package com.ixuea.courses.mymusic.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.Consts;


/**
 * Created by smile on 02/03/2018.
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    /**
     * 找控件
     */
    protected void initViews() {
    }

    /**
     *设置数据
     */
    protected void initDatas() {

    }

    /**
     * 绑定监听器
     */
    protected void initListener() {
    }

    private void init() {
        initViews();
        initDatas();
        initListener();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        init();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }


    protected void startActivity(Class<?> clazz) {
        startActivity(new Intent(getActivity(),clazz));
    }

    protected void startActivityAfterFinishThis(Class<?> clazz) {
        startActivity(new Intent(getActivity(),clazz));
        finish();
    }

    protected void startActivityExtraId(Class<?> clazz, String id) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtra(Consts.ID,id);
        startActivity(intent);
    }

    protected void startActivityExtraString(Class<?> clazz, String string) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtra(Consts.STRING,string);
        startActivity(intent);
    }

    protected BaseActivity getActivity() {
        return this;
    }

    public void showLoading() {
        showLoading(getResources().getString(R.string.loading));
    }

    public void showLoading(String message) {
//        if (getActivity() != null && !getActivity().isFinishing()) {
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("提示");
//            progressDialog.setMessage(message);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
    }

    public void showLoading(int resId) {
        showLoading(getResources().getString(resId));
    }

    public void hideLoading() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.hide();
//        }
    }
}
