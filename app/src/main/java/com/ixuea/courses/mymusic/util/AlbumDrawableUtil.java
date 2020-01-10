package com.ixuea.courses.mymusic.util;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by smile on 2018/6/12.
 */

public class AlbumDrawableUtil {
    private static final int INDEX_BACKGROUND = 0;
    private static final int INDEX_FOREGROUND = 1;
    private static final int DEFAULT_DURATION_ANIMATION = 300;
    private final LayerDrawable layerDrawable;
    private ValueAnimator animator;

    public AlbumDrawableUtil(Drawable backgroundDrawable, Drawable foregroundDrawable) {
        Drawable[] drawables = new Drawable[2];

        drawables[INDEX_BACKGROUND] = backgroundDrawable;
        drawables[INDEX_FOREGROUND] = foregroundDrawable;

        layerDrawable = new LayerDrawable(drawables);

        initAnimation();
    }

    private void initAnimation() {
        animator = ValueAnimator.ofFloat(0f, 255.0f);
        animator.setDuration(DEFAULT_DURATION_ANIMATION);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float foregroundAlpha = (float)animation.getAnimatedValue();
                //前景慢慢变的不透明
                layerDrawable.getDrawable(INDEX_FOREGROUND).setAlpha((int) foregroundAlpha);
            }
        });
    }

    public Drawable getDrawable() {
        return layerDrawable;
    }

    public void start() {
        animator.start();
    }
}
