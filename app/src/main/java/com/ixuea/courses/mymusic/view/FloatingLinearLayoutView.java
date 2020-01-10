package com.ixuea.courses.mymusic.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Lyric;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.listener.OnFloatingListener;
import com.ixuea.courses.mymusic.parser.LyricsParser;
import com.ixuea.courses.mymusic.util.ColorUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;

/**
 * Created by smile on 2018/6/3.
 */

public class FloatingLinearLayoutView extends LinearLayout {
    private static final String TAG = "FloatingLayoutView";

    /**
     * 歌词View背景，黑色
     */
    private static final int DEFAULT_GLOBAL_LYRIC_BG = Color.BLACK;

    /**
     * 歌词View背景透明度，半透明
     */
    private static final int DEFAULT_GLOBAL_LYRIC_ALPHA = 204;

    private static final int[] LYRIC_COLORS = new int[]{R.color.lyric_color0,
            R.color.lyric_color1, R.color.lyric_color2, R.color.lyric_color3, R.color.lyric_color4};

    private ImageView iv_logo;
    private ImageView iv_close;
    private ImageView iv_settings;
    private ImageView iv_lock;
    private ImageView iv_previous;
    private ImageView iv_play;
    private ImageView iv_next;
    private RelativeLayout rl_header_container;
    private LinearLayout ll_play_container;
    private LinearLayout ll_lyric_edit_container;
    private MultiLineLyricView lv;
    private OnFloatingListener onFloatingListener;
    private boolean isIntercept;
    private float lastY;
    private float lastX;
    private int touchSlop;
    private OnTouchDragListener onTouchDragListener;
    private ImageView iv_font_size_small;
    private ImageView iv_font_size_large;
    private SharedPreferencesUtil sp;
    private ImageView iv_lyric_color0;
    private ImageView iv_lyric_color1;
    private ImageView iv_lyric_color2;
    private ImageView iv_lyric_color3;
    private ImageView iv_lyric_color4;

    private RelativeLayout lyric_color0_container;
    private RelativeLayout lyric_color1_container;
    private RelativeLayout lyric_color2_container;
    private RelativeLayout lyric_color3_container;
    private RelativeLayout lyric_color4_container;

    public FloatingLinearLayoutView(Context context) {
        super(context);
        init();
    }

    public FloatingLinearLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingLinearLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatingLinearLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();

        sp = SharedPreferencesUtil.getInstance(getContext());

        initViews();

        initDatas();

        initListener();
    }

    private void initDatas() {
        //设置配置的值
        lv.setFontSize(sp.getGlobalLyricFontSize());

        updateLyricViewColor(sp.getGlobalLyricTextColorIndex());
    }

    private void initListener() {
        //桌面歌词设置
        iv_settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_lyric_edit_container.getVisibility() == View.VISIBLE) {
                    ll_lyric_edit_container.setVisibility(GONE);
                } else {
                    ll_lyric_edit_container.setVisibility(VISIBLE);
                }
            }
        });

        //歌词View点击
        OnClickListener thisClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_logo.getVisibility() == View.VISIBLE) {
                    //标准样式
                    simpleStyle();
                } else {
                    //简单样式
                    normalStyle();
                }
            }
        };
        this.setOnClickListener(thisClick);

        //播放控制
        iv_previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFloatingListener != null) {
                    onFloatingListener.onPrevious();
                }
            }
        });

        iv_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFloatingListener != null) {
                    onFloatingListener.onPlayClick();
                }
            }
        });

        iv_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFloatingListener != null) {
                    onFloatingListener.onNext();
                }
            }
        });

        iv_font_size_small.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentSize = lv.fontSizeDecrement();
                sp.setGlobalLyricFontSize(currentSize);
            }
        });

        iv_font_size_large.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentSize = lv.fontSizeIncrement();
                sp.setGlobalLyricFontSize(currentSize);
            }
        });

        //颜色切换
        OnClickListener onChangeColorListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lyric_color0_container:
                        updateLyricViewColor(0);
                        break;
                    case R.id.lyric_color1_container:
                        updateLyricViewColor(1);
                        break;
                    case R.id.lyric_color2_container:
                        updateLyricViewColor(2);
                        break;
                    case R.id.lyric_color3_container:
                        updateLyricViewColor(3);
                        break;
                    case R.id.lyric_color4_container:
                        updateLyricViewColor(4);
                        break;
                }
            }
        };
        lyric_color0_container.setOnClickListener(onChangeColorListener);
        lyric_color1_container.setOnClickListener(onChangeColorListener);
        lyric_color2_container.setOnClickListener(onChangeColorListener);
        lyric_color3_container.setOnClickListener(onChangeColorListener);
        lyric_color4_container.setOnClickListener(onChangeColorListener);

    }

    private void updateLyricViewColor(int index) {
        int color = getResources().getColor(LYRIC_COLORS[index]);
        lv.setTextColor(color);
        sp.setGlobalLyricTextColorIndex(index);

        switch (index) {
            case 0:
                checked(iv_lyric_color0);
                unchecked(iv_lyric_color1);
                unchecked(iv_lyric_color2);
                unchecked(iv_lyric_color3);
                unchecked(iv_lyric_color4);
                break;
            case 1:
                unchecked(iv_lyric_color0);
                checked(iv_lyric_color1);
                unchecked(iv_lyric_color2);
                unchecked(iv_lyric_color3);
                unchecked(iv_lyric_color4);
                break;
            case 2:
                unchecked(iv_lyric_color0);
                unchecked(iv_lyric_color1);
                checked(iv_lyric_color2);
                unchecked(iv_lyric_color3);
                unchecked(iv_lyric_color4);
                break;
            case 3:
                unchecked(iv_lyric_color0);
                unchecked(iv_lyric_color1);
                unchecked(iv_lyric_color2);
                checked(iv_lyric_color3);
                unchecked(iv_lyric_color4);
                break;
            case 4:
                unchecked(iv_lyric_color0);
                unchecked(iv_lyric_color1);
                unchecked(iv_lyric_color2);
                unchecked(iv_lyric_color3);
                checked(iv_lyric_color4);
                break;
        }
    }

    private void unchecked(ImageView iv) {
        iv.setVisibility(GONE);
    }

    private void checked(ImageView iv) {
        iv.setVisibility(VISIBLE);
    }

    private void initViews() {
        setBackgroundColor(ColorUtil.parserColor(DEFAULT_GLOBAL_LYRIC_BG, DEFAULT_GLOBAL_LYRIC_ALPHA));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_global_lyric, this, false);
        addView(view);

        //头部
        iv_logo = findViewById(R.id.iv_logo);
        iv_close = findViewById(R.id.iv_close);

        //播放控制
        iv_lock = findViewById(R.id.iv_lock);
        iv_previous = findViewById(R.id.iv_previous);
        iv_play = findViewById(R.id.iv_play);
        iv_next = findViewById(R.id.iv_next);
        iv_settings = findViewById(R.id.iv_settings);

        //容器
        rl_header_container = findViewById(R.id.ll_header_container);
        ll_play_container = findViewById(R.id.ll_play_container);
        ll_lyric_edit_container = findViewById(R.id.ll_lyric_edit_container);

        //歌词
        lv = findViewById(R.id.lv);

        //字体大小控制
        iv_font_size_small = findViewById(R.id.iv_font_size_small);
        iv_font_size_large = findViewById(R.id.iv_font_size_large);

        //颜色
        lyric_color0_container = findViewById(R.id.lyric_color0_container);
        lyric_color1_container = findViewById(R.id.lyric_color1_container);
        lyric_color2_container = findViewById(R.id.lyric_color2_container);
        lyric_color3_container = findViewById(R.id.lyric_color3_container);
        lyric_color4_container = findViewById(R.id.lyric_color4_container);

        iv_lyric_color0 = findViewById(R.id.iv_lyric_color0);
        iv_lyric_color1 = findViewById(R.id.iv_lyric_color1);
        iv_lyric_color2 = findViewById(R.id.iv_lyric_color2);
        iv_lyric_color3 = findViewById(R.id.iv_lyric_color3);
        iv_lyric_color4 = findViewById(R.id.iv_lyric_color4);
    }

    public void setOnCloseListener(OnClickListener onClickListener) {
        iv_close.setOnClickListener(onClickListener);
    }

    public void setOnLogoClickListener(OnClickListener onClickListener) {
        iv_logo.setOnClickListener(onClickListener);
    }

    public void setOnLockClickListener(OnClickListener onClickListener) {
        iv_lock.setOnClickListener(onClickListener);
    }

    /**
     * 简单样式，只有歌词
     */
    public void simpleStyle() {
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        iv_logo.setVisibility(GONE);
        iv_close.setVisibility(GONE);
        ll_play_container.setVisibility(GONE);
        ll_lyric_edit_container.setVisibility(GONE);
    }

    /**
     * 标准样式，都显示
     */
    public void normalStyle() {
        setBackgroundColor(ColorUtil.parserColor(DEFAULT_GLOBAL_LYRIC_BG, DEFAULT_GLOBAL_LYRIC_ALPHA));
        iv_logo.setVisibility(VISIBLE);
        iv_close.setVisibility(VISIBLE);
        ll_play_container.setVisibility(VISIBLE);
    }

    public void setOnFloatingListener(OnFloatingListener onFloatingListener) {
        this.onFloatingListener = onFloatingListener;
    }

    public void setPlay(boolean isPlaying) {
        iv_play.setImageResource(isPlaying ? R.drawable.ic_global_pause : R.drawable.ic_global_play);
    }

    public void onProgress(long progress) {
        lv.show(progress);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isIntercept = false;
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - lastX) > touchSlop && Math.abs(ev.getY() - lastY) > touchSlop) {
                    //拖拽
                    isIntercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            //这里监听不到ACTION_DOWN，因为onInterceptTouchEvent方法没有拦截
            case MotionEvent.ACTION_MOVE:
                float distanceX = event.getX() - lastX;
                float distanceY = event.getY() - lastY;
                if (Math.abs(distanceY) > touchSlop) {
                    //拖拽
                    if (onTouchDragListener != null) {
                        float rawY = event.getRawY();
                        onTouchDragListener.onMove((int) (rawY - lastY));
                    }
                    Log.d(TAG, "onTouchEvent: " + distanceY);
                }
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnTouchDragListener(OnTouchDragListener onTouchDragListener) {
        this.onTouchDragListener = onTouchDragListener;
    }

    public void update(Song song) {
        Lyric lyric = song.getLyric();
        if (lyric != null) {
            LyricsParser parser = LyricsParser.parse(lyric.getStyle(), lyric.getContent());
            parser.parse();
            if (parser.getLyric() != null) {
                lv.setData(parser.getLyric());
            }
        }
    }

    public interface OnTouchDragListener {
        void onMove(int y);
    }
}
