package com.ixuea.courses.mymusic.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.DensityUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * Created by smile on 2018/6/22.
 */

public class RecordBackgroundView extends View {
    /**
     * Cd白圈背景的比例
     */
    public static final float CD_BG_SCALE = 1.3F;

    /**
     * 白圈
     */
    private Drawable cdBg;

    /**
     * CD白圈背景到顶部的比例
     */
    public static final float CD_BG_TOP_SCALE = 17.052F;

    public RecordBackgroundView(Context context) {
        super(context);
        init();
    }

    public RecordBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecordBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        cdBg = getResources().getDrawable(R.drawable.shape_cd_bg);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int widthHalf = measuredWidth / 2;

        int cdBgWidth = (int) (measuredWidth / CD_BG_SCALE);
        int cdBgWidthHalf = cdBgWidth / 2;

        //cd背景
        int cdBgLeft = widthHalf - cdBgWidthHalf;
        int cdBgTop = DensityUtil.dip2px(getContext(), measuredWidth / CD_BG_TOP_SCALE);
        cdBg.setBounds(cdBgLeft, cdBgTop, cdBgLeft + cdBgWidth, cdBgTop + cdBgWidth);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //绘制背景
        cdBg.draw(canvas);

        canvas.restore();
    }

}
