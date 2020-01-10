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
 * 两行歌词，用于在全局歌词显示
 * Created by smile on 2018/5/29.
 */

public class MultiLineLyricView extends View {
    private static final String TAG = "TAG";

    /**
     * 默认歌词字体大小
     */
    private static final float DEFAULT_LYRIC_FONT_SIZE = 18;

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
     * 歌词高亮画笔
     */
    private Paint foregroundTextPaint;

    /**
     * 当前所在歌词的行号
     */
    private int lineNumber = 0;

    /**
     * 音乐当前的播放位置
     */
    private long position;
    private int fontSize;
    private int textColor;

    /**
     * 当前字，已经播放的时间
     */
    private float wordPlayedTime = 0;

    /**
     * 当前行歌词已经唱过的宽度，也就是歌词高亮宽度
     */
    private float lineLyricPlayedWidth;

    /**
     * 当前播放时间点，在该行歌词的第几个字
     */
    private int lyricCurrentWordIndex = -1;

    /**
     * 行与行之间的高度
     */
    private float lineSpaceHeight=30;

    public MultiLineLyricView(Context context) {
        super(context);
        init();
    }

    public MultiLineLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiLineLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiLineLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        fontSize= DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_FONT_SIZE);
        textColor= Color.parseColor("#F93F45");

        //初始化画笔
        backgroundTextPaint = new Paint();
        backgroundTextPaint.setDither(true);
        backgroundTextPaint.setAntiAlias(true);
        backgroundTextPaint.setColor(Color.WHITE);

        foregroundTextPaint = new Paint();
        foregroundTextPaint.setDither(true);
        foregroundTextPaint.setAntiAlias(true);


        updateTextColor();
        updateTextSize();
    }

    private void updateTextSize() {
        backgroundTextPaint.setTextSize(fontSize);
        foregroundTextPaint.setTextSize(fontSize);
    }

    private void updateTextColor() {
        foregroundTextPaint.setColor(textColor);
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
        float textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
        float textHeight = getTextHeight(backgroundTextPaint);

        Paint.FontMetrics fontMetrics = backgroundTextPaint.getFontMetrics();
        //TextView绘制值从baseLine开始，而不是左上角
        float centerY = Math.abs(fontMetrics.top);

        float x = (getMeasuredWidth()-textWidth)/2;
        float y = centerY;

        //绘制第一行全部文字
        canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);

        if (lineNumber<lyricsLines.size()-1) {
            //绘制第二行全部文字
            Line nextLine=lyricsLines.get(lineNumber+1);
            float nextTextWidth = getTextWidth(backgroundTextPaint, nextLine.getLineLyrics());
            float nextX = (getMeasuredWidth()-nextTextWidth)/2;
            canvas.drawText(nextLine.getLineLyrics(), nextX, y+Math.abs(fontMetrics.top)+lineSpaceHeight, backgroundTextPaint);
        }


        //当前歌词高亮
        if (lyric.isAccurate()) {
            canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);

            if (lyricCurrentWordIndex == -1) {
                //该行已经播放完了
                lineLyricPlayedWidth=textWidth;
            } else {
                String[] lyricsWord = line.getLyricsWord();
                int[] wordDuration = line.getWordDuration();

                //获取当前时间前面的文字
                String beforeText=line.getLineLyrics().substring(0,lyricCurrentWordIndex);
                float beforeTextWidth=getTextWidth(foregroundTextPaint, beforeText);

                //当前字
                String currentWord=lyricsWord[lyricCurrentWordIndex];
                float currentWordTextWidth=getTextWidth(foregroundTextPaint, currentWord);

                //当前字已经演唱的宽度
                float currentWordWidth = currentWordTextWidth / wordDuration[lyricCurrentWordIndex] * wordPlayedTime;

                lineLyricPlayedWidth = beforeTextWidth + currentWordWidth;
            }

            canvas.save();
            //裁剪一个矩形用来绘制已经唱的歌词
            canvas.clipRect(x, y - textHeight, x + lineLyricPlayedWidth,
                    y + textHeight);


            //这个矩形包是文字的高度+行高
            //canvas.drawRect(x, y - textHeight, x + lineLyricPlayedWidth,
            //        y + textHeight,foregroundTextPaint);

            canvas.drawText(line.getLineLyrics(), x, y, foregroundTextPaint);

            canvas.restore();
        } else {
            //精确到行
            canvas.drawText(line.getLineLyrics(), x, y, foregroundTextPaint);
        }
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

        float centerX = (getMeasuredWidth()-textWidth)/2;
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

        int newLineNumber = lyric.getLineNumber(position);

        if (newLineNumber != lineNumber) {
            lineNumber = newLineNumber;
            //重置变量
            lineLyricPlayedWidth = 0;
            lyricCurrentWordIndex = 0;

        }

        if (lyric.isAccurate()) {
            //获取当前时间是该行的的第几个字
            lyricCurrentWordIndex = lyric.getWordIndex(lineNumber, position);

            //获取当前时间的该字，已经播放的时间
            wordPlayedTime = lyric.getWordPlayedTime(lineNumber, position);
        }
        invalidate();
    }

    private boolean isEmptyLyric() {
        return lyric == null;
    }

    public void setFontSize(int fontSize) {
        this.fontSize=fontSize;
        updateTextSize();
    }

    public void setTextColor(int textColor) {
        this.textColor=textColor;
        updateTextColor();
    }

    public int fontSizeIncrement() {
        fontSize++;
        updateTextSize();
        return fontSize;
    }

    public int fontSizeDecrement() {
        fontSize--;
        updateTextSize();
        return fontSize;
    }
}
