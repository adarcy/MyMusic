package com.ixuea.courses.mymusic.span;

import android.text.TextPaint;

/**
 * Created by smile on 2018/6/9.
 */

public abstract class ClickableSpan extends android.text.style.ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        //去掉下划线
        //ds.setUnderlineText(true);
    }


}
