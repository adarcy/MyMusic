package com.ixuea.courses.mymusic.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.ixuea.courses.mymusic.listener.OnLyricClickListener;
import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.parser.domain.Lyric;
import com.ixuea.courses.mymusic.util.DensityUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * 滚动歌词，用在播放界面
 * Created by smile on 2018/5/29.
 */
@Deprecated
public class LyricView extends View {
    private static final String TAG = "TAG";

    /**
     * 默认歌词字体大小
     */
    private static final float DEFAULT_LYRIC_FONT_SIZE = 15;

    /**
     * 默认时间线，时间，点击按钮字体大小
     */
    private static final float DEFAULT_TIME_FONT_SIZE = 15;

    /**
     * 默认时间，播放文字到屏幕两边的距离
     */
    private static final float DEFAULT_LYRIC_MARGIN = 16;

    /**
     * 播放按钮，增大区域
     */
    private static final float DEFAULT_PLAY_TEXT_AREA = 10;

    /**
     * 默认分割线到两边文字的距离
     */
    private static final float DEFAULT_LYRIC_TIME_LINE_MARGIN_LEFT = 5;

    /**
     * 默认歌词显示的内容
     */
    private static final String DEFAULT_TIP_TEXT = "我的云音乐,听你想听";

    /**
     * 拖拽的时候，播放按钮，当然可以换成图片
     */
    private static final String PLAY_TEXT = "播放";

    /**
     * 拖拽后，多少秒继续滚动歌词
     */
    private static final long DEFAULT_HIDE_DRAG_TIME = 3000;

    /**
     * 事件类型
     */
    private static final int MSG_HIDE_TIME_LINE = 0;

    /**
     * 点击了歌词旁边的播放
     */
    private OnLyricClickListener onLyricClickListener;

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
     * 时间线画笔
     */
    private Paint timeLinePaint;

    /**
     * 时间线，左边时间画笔
     */
    private Paint timePaint;

    /**
     * 播放文字画笔
     */
    private Paint playPaint;

    /**
     * 歌词高亮画笔
     */
    private Paint foregroundTextPaint;
    /**
     * 垂直方向的，空行高度
     */
    private float lineSpaceHeight = 35;

    /**
     * 当前所在歌词的行号
     */
    private int lineNumber = 0;


    /**
     * 当前行已经播放的宽度
     */
    private float foregroundWidth = 0;

    /**
     * 当前播放时间点，在该行歌词的第几个字
     */
    private int lyricCurrentWordIndex = -1;

    /**
     * 当前字，已经播放的时间
     */
    private float wordPlayedTime = 0;

    /**
     * 当前行歌词已经唱过的宽度，也就是歌词高亮宽度
     */
    private float lineLyricPlayedWidth;

    /**
     * 当前播放位置的歌词的偏移
     */
    private float offsetY = 0;

    /**
     * 滚动用的属性动画
     */
    private ValueAnimator valueAnimator;

    /**
     * 音乐当前的播放位置
     */
    private long position;

    /**
     * 上一次触摸点
     */
    private float lastY;

    /**
     * 是否是拖拽模式
     */
    private boolean isDrag;

    /**
     * 歌词旁边播放文字的矩形，用来判断是否点击到了
     */
    private Rect playRect = new Rect();

    /**
     * 歌词的左右间距
     */
    private int lyricMargin;

    /**
     * 时间线左右的间距
     */
    private int lyricTimeLineMarginLeft;

    /**
     * 用于多少秒后隐藏时间线
     */
    private TimerTask timerTask;
    private Timer timer;

    /**
     * 最小滑动值
     */
    private int touchSlop;

    /**
     * 播放按钮增大区域
     */
    private int playTextArea;

    /**
     * 用来检测是否是长按事件的Runnable
     */
    private CheckForLongPress mPendingCheckForLongPress;

    /**
     * 长按事件是否已经返回了true
     */
    private boolean mHasPerformedLongPress;


    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        //将一些值转为px
        lyricMargin = DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_MARGIN);
        lyricTimeLineMarginLeft = DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_TIME_LINE_MARGIN_LEFT);
        playTextArea = DensityUtil.dip2px(getContext(), DEFAULT_PLAY_TEXT_AREA);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();

        //初始化画笔
        backgroundTextPaint = new Paint();
        backgroundTextPaint.setDither(true);
        backgroundTextPaint.setAntiAlias(true);
        backgroundTextPaint.setTextSize(DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_FONT_SIZE));
        backgroundTextPaint.setColor(Color.WHITE);

        foregroundTextPaint = new Paint();
        foregroundTextPaint.setDither(true);
        foregroundTextPaint.setAntiAlias(true);
        foregroundTextPaint.setTextSize(DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_FONT_SIZE));
        foregroundTextPaint.setColor(Color.RED);


        timeLinePaint = new Paint();
        timeLinePaint.setDither(true);
        timeLinePaint.setAntiAlias(true);
        timeLinePaint.setTextSize(DensityUtil.dip2px(getContext(), DEFAULT_TIME_FONT_SIZE));
        timeLinePaint.setColor(Color.RED);

        timePaint = new Paint();
        timePaint.setDither(true);
        timePaint.setAntiAlias(true);
        timePaint.setTextSize(DensityUtil.dip2px(getContext(), DEFAULT_TIME_FONT_SIZE));
        timePaint.setColor(Color.RED);

        playPaint = new Paint();
        playPaint.setDither(true);
        playPaint.setAntiAlias(true);
        playPaint.setTextSize(DensityUtil.dip2px(getContext(), DEFAULT_TIME_FONT_SIZE));
        playPaint.setColor(Color.RED);

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

        // 如果在拖拽，画时间线
        if (isDrag) {
            drawIndicator(canvas);
        }

        canvas.restore();
    }

    private void drawIndicator(Canvas canvas) {
        //获取当前滑动到的歌词播放行
        int lineNumber = getCurrentLineNumber();

        //获取这一行的开始时间
        int startTime = (int) lyricsLines.get(lineNumber).getStartTime();
        String startTimeString = TimeUtil.parseString(startTime);

        //绘制左侧，播放按钮文字
        float playTextWidth = playPaint.measureText(PLAY_TEXT);
        int playTextHeight = (int) getTextHeight(playPaint);

        int playTextX = lyricMargin;
        int playTextY = (getMeasuredHeight() - playTextHeight) / 2;

        Paint.FontMetricsInt fmi = playPaint.getFontMetricsInt();
        canvas.drawText(PLAY_TEXT, playTextX, playTextY + Math.abs(fmi.top), playPaint);

        //增大文字的矩形区域，这样就更容易电击
        playRect.left = playTextX - playTextArea;
        playRect.right = (int) (playTextX + playTextWidth + playTextArea);
        playRect.top = playTextY - playTextArea;
        playRect.bottom = playTextY + playTextHeight + playTextArea;

        //绘制右侧时间
        float timeTextWidth = timePaint.measureText(startTimeString);
        float timeTextHeight = getTextHeight(timePaint);

        fmi = timePaint.getFontMetricsInt();

        float timeTextX = (int) (getMeasuredWidth() - lyricMargin - timeTextWidth);

        //绘制文字时，他不是从位置顶部绘制，而是从baseline位置开始绘制
        //所以这就是为什么我们要在高级View的课程中讲解TextView的原因了
        float timeTextY = (getMeasuredHeight() - timeTextHeight) / 2 + Math.abs(fmi.top);
        canvas.drawText(startTimeString, timeTextX, timeTextY, timeLinePaint);


        //绘制中间时间线
        float timeLineX = lyricMargin + playTextWidth + lyricTimeLineMarginLeft;
        float timeLineY = getMeasuredHeight() / 2;
        ////画时间线
        canvas.drawLine(timeLineX, timeLineY, getWidth() - timeTextWidth - lyricTimeLineMarginLeft - lyricMargin, timeLineY, timeLinePaint);

    }

    /**
     * 获取当前滚动所在的行号
     *
     * @return
     */
    public int getCurrentLineNumber() {
        float scrollY = offsetY + getLineHeight(backgroundTextPaint) / 2;
        int lineNumber = (int) (scrollY / getLineHeight(backgroundTextPaint));

        if (lineNumber >= lyricsLines.size()) {
            lineNumber = lyricsLines.size() - 1;
        } else if (lineNumber < 0) {
            lineNumber = 0;
        }
        return lineNumber;
    }


    private void drawLyricText(Canvas canvas) {
        //在当前位置绘制正在演唱的歌词
        Line line = lyricsLines.get(lineNumber);

        //当前歌词的宽高
        float textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
        float textHeight = getTextHeight(backgroundTextPaint);

        float centerY = (getMeasuredHeight() - textHeight) / 2 + lineNumber * getLineHeight(backgroundTextPaint) - offsetY;

        float x = (getMeasuredWidth() - textWidth) / 2;
        float y = centerY;

        //当前歌词高亮
        if (lyric.isAccurate()) {
            canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);

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


        //绘制前面的歌词
        for (int i = lineNumber - 1; i > 0; i--) {
            //从当前行的上一行开始绘制
            line = lyricsLines.get(i);

            //当前歌词的宽高
            textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
            textHeight = getTextHeight(backgroundTextPaint);


            x = (getMeasuredWidth() - textWidth) / 2;
            y = centerY - (lineNumber - i) * getLineHeight(backgroundTextPaint);

            if (y < getLineHeight(backgroundTextPaint)) {
                //超出了View顶部，不再绘制
                break;
            }

            canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);
        }

        //绘制后面的歌词
        for (int i = lineNumber + 1; i < lyricsLines.size(); i++) {
            //从当前行的下一行开始绘制
            line = lyricsLines.get(i);

            //当前歌词的宽高
            textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
            textHeight = getTextHeight(backgroundTextPaint);


            x = (getMeasuredWidth() - textWidth) / 2;
            y = centerY + (i - lineNumber) * getLineHeight(backgroundTextPaint);

            if (y + getLineHeight(backgroundTextPaint) > getHeight()) {
                //超出了View底部，不再绘制
                break;
            }

            canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);
        }

    }

    /**
     * 获取一行歌词的高度
     *
     * @return 歌词的文本高度+空行高度
     */
    private float getLineHeight(Paint paint) {
        return Math.abs(getTextHeight(backgroundTextPaint) + lineSpaceHeight);
    }

    private void drawDefaultText(Canvas canvas) {
        float textWidth = getTextWidth(backgroundTextPaint, DEFAULT_TIP_TEXT);
        float textHeight = getTextHeight(backgroundTextPaint);

        float centerX = (getWidth() - textWidth) / 2;
        float centerY = (getHeight() - textHeight) / 2;

        canvas.drawText(DEFAULT_TIP_TEXT, centerX, centerY, foregroundTextPaint);
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

        if (isDrag) {
            //如果在拖拽，返回
            return;
        }

        this.position = position;

        int newLineNumber = lyric.getLineNumber(position);

        if (newLineNumber != lineNumber) {
            //重置变量
            lineLyricPlayedWidth = 0;
            lyricCurrentWordIndex = 0;

            lineNumber = newLineNumber;

            //要滚动到的距离
            float distanceY = lineNumber * getLineHeight(backgroundTextPaint);
            //动画的形式滚动到当前行
            smoothScrollTo(distanceY);
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

    private void smoothScrollTo(float distanceY) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(offsetY, distanceY);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offsetY = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEmptyLyric()) {
            if (MotionEvent.ACTION_UP == event.getActionMasked()) {
                performClick();
            }
            return true;
        }
        final float x = event.getX();
        final float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                //清除上一次长按事件的标志
                mHasPerformedLongPress = false;
                //设置为按下状态
                setPressed(true, x, y);
                //发送延时消息
                checkForLongClick(0, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = y - lastY;
                if (Math.abs(distance) > touchSlop) {
                    isDrag = true;
                    //如果移动就取消按下
                    setPressed(false, x, y);
                }
                offsetY -= distance;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                //设置为抬起
                setPressed(false, x, y);
                onActionUp(event);
                break;
            default:
                break;
        }

        invalidate();
        return true;
    }

    private void setPressed(boolean pressed, float x, float y) {
        if (pressed) {
            drawableHotspotChanged(x, y);
        }

        setPressed(pressed);
    }

    /**
     * 创建一个长按事件Runnable，并发送一个延时消息
     * @param delayOffset
     * @param x
     * @param y
     */
    private void checkForLongClick(int delayOffset, float x, float y) {
        mHasPerformedLongPress = false;

        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        mPendingCheckForLongPress.setAnchor(x, y);
        mPendingCheckForLongPress.rememberPressedState();
        postDelayed(mPendingCheckForLongPress,
                ViewConfiguration.getLongPressTimeout() - delayOffset);
    }

    /**
     * 用来检测长按事件
     */
    private final class CheckForLongPress implements Runnable {
        private float mX;
        private float mY;
        private boolean mOriginalPressedState;

        @Override
        public void run() {
            if ((mOriginalPressedState == isPressed())) {
                if (performLongClick(mX, mY)) {
                    mHasPerformedLongPress = true;
                }
            }
        }

        public void setAnchor(float x, float y) {
            mX = x;
            mY = y;
        }


        public void rememberPressedState() {
            mOriginalPressedState = isPressed();
        }
    }


    /**
     * 判断是否是播放按钮
     *
     * @param event
     * @return
     */
    private boolean isPlayClick(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        return playRect.contains(x, y);

    }

    private void onActionUp(MotionEvent event) {
        if (isDrag) {
            if (isPlayClick(event)) {
                isDrag = false;
                invalidate();

                if (onLyricClickListener != null) {
                    //获取当前滑动到的歌词播放行
                    int scrollLrcLineNum = getCurrentLineNumber();
                    long startTime = lyricsLines.get(scrollLrcLineNum).getStartTime();
                    onLyricClickListener.onLyricClick(startTime);
                }
            } else {
                startHideTimeLine();
            }
        } else {
            if (!mHasPerformedLongPress) {
                //如果长按事件没有处理，在执行点击事件
                removeLongPressCallback();
                performClick();
            }
        }
    }


    private void removeLongPressCallback() {
        if (mPendingCheckForLongPress != null) {
            removeCallbacks(mPendingCheckForLongPress);
        }
    }

    private void startHideTimeLine() {
        cancelTask();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.obtainMessage(MSG_HIDE_TIME_LINE).sendToTarget();
            }
        };
        timer = new Timer();

        timer.schedule(timerTask, DEFAULT_HIDE_DRAG_TIME);
    }

    private void cancelTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    public void setOnLyricClickListener(OnLyricClickListener onLyricClickListener) {
        this.onLyricClickListener = onLyricClickListener;
    }

    //这样创建有内存泄漏，在性能优化我们具体讲解
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE_TIME_LINE:
                    isDrag = false;
                    invalidate();
                    break;
            }
        }
    };
}
