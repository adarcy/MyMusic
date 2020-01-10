package com.ixuea.courses.mymusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.util.DensityUtil;
import com.ixuea.courses.mymusic.util.LogUtil;

public class LyricLineView extends View {
    /**
     * 默认歌词字体大小
     */
    private static final float DEFAULT_LYRIC_FONT_SIZE = 16;

    ///**
    // * 当前歌词字体大小
    // */
    //private static final float DEFAULT_CURRENT_LYRIC_FONT_SIZE = 18;

    /**
     当前歌词行是否选中，也就是唱到这一行了
     */
    private boolean isSelected;

    /**
     * 当前歌词是否是精确模式
     */
    private boolean isAccurate;

    /**
     * 当前播放时间点，在该行歌词的第几个字
     */
    private int lyricCurrentWordIndex = -1;

    /**
     * 当前行歌词已经唱过的宽度，也就是歌词高亮宽度
     */
    private float lineLyricPlayedWidth;

    /**
     * 当前字，已经播放的时间
     */
    private float wordPlayedTime = 0;

    private Paint backgroundTextPaint;
    private Paint foregroundTextPaint;
    private Paint testPaint;
    private Line line;

    public LyricLineView(Context context) {
        super(context);
        init();
    }


    public LyricLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LyricLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //初始化画笔
        backgroundTextPaint = new Paint();
        backgroundTextPaint.setDither(true);
        backgroundTextPaint.setAntiAlias(true);
        backgroundTextPaint.setTextSize(DensityUtil.dip2px(getContext(),DEFAULT_LYRIC_FONT_SIZE));
        backgroundTextPaint.setColor(Color.WHITE);

        foregroundTextPaint = new Paint();
        foregroundTextPaint.setDither(true);
        foregroundTextPaint.setAntiAlias(true);
        foregroundTextPaint.setTextSize(DensityUtil.dip2px(getContext(),DEFAULT_LYRIC_FONT_SIZE));
        foregroundTextPaint.setColor(getResources().getColor(R.color.main_color));

        testPaint = new Paint();
        testPaint.setDither(true);
        testPaint.setAntiAlias(true);
        testPaint.setColor(Color.GREEN);
        testPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //如果没有歌词就返回
        if (line == null) {
            return;
        }

        //LogUtil.d("LyricLineView onDraw isSelected:"+isSelected+" this:"+this);

        canvas.save();

        //绘制背景文字

        //当前歌词的宽高
        float textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
        float textHeight = getTextHeight(backgroundTextPaint);

        Paint.FontMetrics fontMetrics = backgroundTextPaint.getFontMetrics();

        //TextView绘制值从baseLine开始，而不是左上角
        float centerY = (getMeasuredHeight() - textHeight) / 2 + Math.abs(fontMetrics.top);

        //水平中心位置
        float centerX = (getMeasuredWidth() - textWidth) / 2;

        //精确到行
        canvas.drawText(line.getLineLyrics(),centerX,centerY,backgroundTextPaint);

        if (!isSelected) {
            //如果没有选择，就不用绘制高亮
            canvas.restore();
            return;
        }

        if (isAccurate) {
            //精确到字歌词

            if (lyricCurrentWordIndex == -1) {
                //该行已经播放完了
                lineLyricPlayedWidth = textWidth;
            } else {
                String[] lyricsWord = line.getLyricsWord();
                int[] wordDuration = line.getWordDuration();

                //获取当前时间前面的文字
                String beforeText = line.getLineLyrics().substring(0, lyricCurrentWordIndex);
                float beforeTextWidth = getTextWidth(foregroundTextPaint, beforeText);

                //当前字
                String currentWord = lyricsWord[lyricCurrentWordIndex];
                float currentWordTextWidth = getTextWidth(foregroundTextPaint, currentWord);

                //当前字已经演唱的宽度
                float currentWordWidth = currentWordTextWidth / wordDuration[lyricCurrentWordIndex] * wordPlayedTime;

                lineLyricPlayedWidth = beforeTextWidth + currentWordWidth;
            }

            LogUtil.d("LyricLineView onDraw lineLyricPlayedWidth:"+lineLyricPlayedWidth);

            //裁剪一个矩形用来绘制已经唱的歌词
            //canvas.clipRect(centerX, centerY - textHeight, centerX + lineLyricPlayedWidth,
            //        centerY + textHeight);

            canvas.clipRect(centerX, 0, centerX + lineLyricPlayedWidth,
                    getMeasuredHeight());


            //这个矩形包是文字的高度+行高
            //canvas.drawRect(centerX, centerY - textHeight, centerX + lineLyricPlayedWidth,
            //        centerY + textHeight,foregroundTextPaint);

            //canvas.drawRect(centerX, 0, centerX + lineLyricPlayedWidth,
            //        getMeasuredHeight(),testPaint);

            canvas.drawText(line.getLineLyrics(), centerX, centerY, foregroundTextPaint);

        } else {
            //精确到行歌词
            canvas.drawText(line.getLineLyrics(), centerX, centerY, foregroundTextPaint);
        }

        //if (精确歌词) {
        //} else {
        //    if (isSelected) {
        //        //精确到行歌词
        //        canvas.drawText(line.getLineLyrics(), centerX, centerY, foregroundTextPaint);
        //    } else {
        //        canvas.drawText(line.getLineLyrics(),centerX,centerY,backgroundTextPaint);
        //    }
        //}

        canvas.restore();
    }

    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //    return (float) Math.ceil(fontMetrics.descent-fontMetrics.top)+2;
        return (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
    }

    private float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setLineSelected(boolean selected) {
        isSelected = selected;
        LogUtil.d("LyricLineView setLineSelected:"+isSelected+" this:"+this);
    }

    public void setAccurate(boolean accurate) {
        isAccurate = accurate;
    }

    public void show(long position) {
        LogUtil.d("LyricLineView show:"+position);

        invalidate();
    }

    public void setLyricCurrentWordIndex(int lyricCurrentWordIndex) {
        this.lyricCurrentWordIndex = lyricCurrentWordIndex;
    }

    public void setWordPlayedTime(float wordPlayedTime) {
        this.wordPlayedTime = wordPlayedTime;
    }
}
