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

import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.parser.domain.Lyric;
import com.ixuea.courses.mymusic.util.DensityUtil;

import java.util.TreeMap;

/**
 * Created by smile on 2018/6/23.
 */

/**
 * 单行歌词View，用在了迷你控制器上
 * Created by smile on 2018/5/29.
 */

public class LyricSingleLineView extends View {
    private static final String TAG = "TAG";

    /**
     * 默认歌词字体大小
     */
    private static final float DEFAULT_LYRIC_FONT_SIZE = 13;

    /**
     * 默认歌词显示的内容
     */
    private static final String DEFAULT_TIP_TEXT = "我的云音乐,听你想听";

    /**
     * 歌词
     */
    private Lyric lyric;


    /**
     * 每一行歌词
     */
    private TreeMap<Integer, Line> lyricsLines;

    /**
     * 歌词画笔
     */
    private Paint backgroundTextPaint;

    /**
     * 当前所在歌词的行号
     */
    private int lineNumber = 0;

    /**
     * 音乐当前的播放位置
     */
    private long position;

    public LyricSingleLineView(Context context) {
        super(context);
        init();
    }

    public LyricSingleLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricSingleLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LyricSingleLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        //初始化画笔
        backgroundTextPaint = new Paint();
        backgroundTextPaint.setDither(true);
        backgroundTextPaint.setAntiAlias(true);
        backgroundTextPaint.setTextSize(DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_FONT_SIZE));
        backgroundTextPaint.setColor(Color.parseColor("#aaaaaa"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        if (isEmptyLyric()) {
            //如果没有歌词，就绘制默认的歌词
            drawDefaultText(canvas);
        } else {
            drawLyricText(canvas);
        }


        canvas.restore();
    }

    private void drawLyricText(Canvas canvas) {
        //在当前位置绘制正在演唱的歌词
        Line line = lyricsLines.get(lineNumber);

        //当前歌词的宽高
        //float textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
        float textHeight = getTextHeight(backgroundTextPaint);

        Paint.FontMetrics fontMetrics = backgroundTextPaint.getFontMetrics();
        //TextView绘制值从baseLine开始，而不是左上角
        float centerY = (getMeasuredHeight() - textHeight) / 2+Math.abs(fontMetrics.top);

        float x = 0;
        float y = centerY;

        //精确到行
        canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);
    }

    /**
     * 获取一行歌词的高度
     *
     * @return 歌词的文本高度+空行高度
     */
    private float getLineHeight(Paint paint) {
        return Math.abs(getTextHeight(backgroundTextPaint));
    }

    private void drawDefaultText(Canvas canvas) {
        float textWidth = getTextWidth(backgroundTextPaint, DEFAULT_TIP_TEXT);
        float textHeight = getTextHeight(backgroundTextPaint);

        Paint.FontMetrics fontMetrics = backgroundTextPaint.getFontMetrics();

        float centerX = 0;
        float centerY = (getHeight() - textHeight) / 2+Math.abs(fontMetrics.top);

        canvas.drawText(DEFAULT_TIP_TEXT, centerX, centerY, backgroundTextPaint);
    }

    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //    return (float) Math.ceil(fontMetrics.descent-fontMetrics.top)+2;
        return (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
    }

    private float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public void setData(Lyric lyric) {
        this.lyric = lyric;
        this.lyricsLines = lyric.getLyrics();
    }

    public void show(long position) {
        if (isEmptyLyric()) {
            //如果没有歌词，返回
            return;
        }

        this.position = position;

        lineNumber = lyric.getLineNumber(position);

        invalidate();
    }

    private boolean isEmptyLyric() {
        return lyric == null;
    }
}
