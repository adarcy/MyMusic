package com.ixuea.courses.mymusic.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.ixuea.courses.mymusic.R;


/**
 * Created by smile on 02/03/2018.
 */

public class BaseTitleActivity extends BaseCommonActivity {

    //@BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void initViews() {
        super.initViews();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            super.setTitle(title);
        }

    }

    protected void enableBackMenu() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
