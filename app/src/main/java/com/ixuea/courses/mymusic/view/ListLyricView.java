package com.ixuea.courses.mymusic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.LyricAdapter;
import com.ixuea.courses.mymusic.listener.OnLyricClickListener;
import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.parser.domain.Lyric;
import com.ixuea.courses.mymusic.util.LogUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ListLyricView extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener, BaseRecyclerViewAdapter.OnItemClickListener, BaseRecyclerViewAdapter.OnItemLongClickListener {
    /**
     * 事件类型
     */
    private static final int MSG_HIDE_TIME_LINE = 0;

    /**
     * 拖拽后，多少秒继续滚动歌词
     */
    private static final long DEFAULT_HIDE_DRAG_TIME = 4000;

    /**
     * 点击了歌词旁边的播放
     */
    private OnLyricClickListener onLyricClickListener;

    /**
     * 歌词
     */
    private Lyric lyric;

    /**
     * 歌词列表View
     */
    private RecyclerView rv;

    /**
     * 是否是拖拽模式
     */
    private boolean isDrag;

    /**
     * 当前所在歌词的行号
     */
    private int lineNumber = 0;

    /**
     * 歌词滚动偏移
     * 会在运行的时候动态计算
     */
    private int lyricItemOffset;

    /**
     * 默认填充数据行
     */
    private static final int DEFAULT_FILL_LYRIC_COUNT = 5;

    /**
     * 歌词监听器
     */
    private LyricListener lyricListener;

    private LinearLayoutManager layoutManager;
    private LyricAdapter adapter;
    private LinearLayout ll_lyric_drag_container;
    private ImageButton ib_lyric_play;
    private TextView tv_lyric_time;
    private TimerTask timerTask;
    private Timer timer;
    private Line scrollSelectedLyricLine;

    public ListLyricView(Context context) {
        super(context);
        init();
    }

    public ListLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ListLyricView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        initViews();
        initDatas();
        initListeners();
    }

    private void initListeners() {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 当滚动状态改变的时候调用
             * 状态和ViewPager滚动一样
             *
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (SCROLL_STATE_DRAGGING == newState) {
                    //拖拽状态
                    showDragView();
                } else if (SCROLL_STATE_IDLE==newState) {
                    //空闲状态
                    prepareShowScrollLyricView();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //这里的dy是当前这一次滚动的距离
                //向上滚动+
                //向下滚动-


                //当前RecyclerView可视的第一个Item位置
                //+DEFAULT_FILL_LYRIC_COUNT-1，是因为添加了填充
                int firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()+DEFAULT_FILL_LYRIC_COUNT-1;

                LogUtil.d("ListLyricView dy:"+dy+" firstVisibleItemPosition:"+firstVisibleItemPosition);

                if (isDrag) {
                    //拖拽的时候，才处理
                    Object data = adapter.getData(firstVisibleItemPosition);

                    if (data instanceof String) {
                        //字符串，用来填充占位符

                        //判断是在开始，还是末尾
                        if (firstVisibleItemPosition < DEFAULT_FILL_LYRIC_COUNT) {
                            //前面的前面的填充

                            //第一行歌词
                            scrollSelectedLyricLine = (Line) adapter.getData(DEFAULT_FILL_LYRIC_COUNT);
                        } else {
                            //后面的填充

                            //最后一行歌词
                            scrollSelectedLyricLine= (Line) adapter.getData(adapter.getItemCount()-DEFAULT_FILL_LYRIC_COUNT-1);
                        }

                    } else {
                        //真实数据
                        scrollSelectedLyricLine= (Line) data;
                    }

                    //设置当前歌词开始时间
                    tv_lyric_time.setText(TimeUtil.parseString((int) scrollSelectedLyricLine.getStartTime()));
                }
            }
        });

        ib_lyric_play.setOnClickListener(this);

        //这里的Adapter我们自定义了，所以事件直接设置就行了
        //具体的请参考《详解RecyclerView》课程

        //设置Item点击事件
        adapter.setOnItemClickListener(this);

        //设置Item长按事件
        adapter.setOnItemLongClickListener(this);
    }

    /**
     * 延时后继续歌词滚动
     */
    private void prepareShowScrollLyricView() {
        //4秒，这里的时间，大家可以根据需求调整
        cancelTask();
        timerTask=new TimerTask(){
            @Override
            public void run() {
                handler.obtainMessage(MSG_HIDE_TIME_LINE).sendToTarget();
            }
        };

        timer=new Timer();

        timer.schedule(timerTask,DEFAULT_HIDE_DRAG_TIME);
    }

    private void cancelTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask=null;
        }

        if (timer != null) {
            timer.cancel();
            timer=null;
        }
    }

    /**
     * 显示拖拽效果
     */
    private void showDragView() {
        isDrag=true;
        ll_lyric_drag_container.setVisibility(View.VISIBLE);
    }

    private void initDatas() {
        adapter = new LyricAdapter(getContext(), R.layout.item_lyric_line);
        rv.setAdapter(adapter);
    }

    private void initViews() {
        //从布局中加载，好处是
        //以后如果还要添加控件，xml比代码写更简单
        View.inflate(getContext(), R.layout.layout_list_lyric_view, this);

        ll_lyric_drag_container=findViewById(R.id.ll_lyric_drag_container);
        ib_lyric_play=findViewById(R.id.ib_lyric_play);
        tv_lyric_time=findViewById(R.id.tv_lyric_time);

        rv=findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        //获取列表控件高度

        //先移除原来的监听，防止重复添加
        rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        //添加布局监听器
        rv.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 设置歌词数据
     * @param lyric
     */
    public void setData(Lyric lyric) {
        this.lyric=lyric;

        Collection<Line> values = lyric.getLyrics().values();

        ArrayList<Object> valueList = new ArrayList<>();

        //添加填充数据
        addFillData(valueList);

        //添加真实数据
        valueList.addAll(values);

        //添加填充数据
        addFillData(valueList);

        adapter.setAccurate(lyric.isAccurate());
        adapter.setData(valueList);

    }

    private void addFillData(ArrayList<Object> valueList) {
        //我们希望第一行，最后一行歌词，居中
        // 所以要在前面添加一些填充数据
        // 如果要兼容更好，应该根据RecyclerView高度，Item高度算
        // 添加多少个填充数据
        for (int i = 0; i < DEFAULT_FILL_LYRIC_COUNT; i++) {
            valueList.add("fill");
        }
    }

    /**
     * 设置歌词监听器
     * @param onLyricClickListener
     */
    public void setOnLyricClickListener(OnLyricClickListener onLyricClickListener) {
        this.onLyricClickListener = onLyricClickListener;
    }

    /**
     * 获取当前滚动所在的行号
     *
     * @return
     */
    public int getCurrentLineNumber() {
        return 0;
    }

    /**
     * 根据传递进来的时间显示对应的歌词
     * @param position
     */
    public void show(long position) {
        if (isEmptyLyric()) {
            //如果没有歌词，返回
            return;
        }

        if (isDrag) {
            //如果手动拖拽了，就占时不滚动
            return;
        }

        //获取当前时间对应的行
        //+5是因为，前面添加了5个占位Item
        int newLineNumber = lyric.getLineNumber(position)+DEFAULT_FILL_LYRIC_COUNT;
        if (newLineNumber != lineNumber) {
            //滚动到这一行
            scrollToPosition(newLineNumber);

            this.lineNumber=newLineNumber;
        }

        //如果是精确到字歌曲，还需要将时间分发到Cell中
        //因为要计算唱到那个字了
        if (lyric.isAccurate()) {
            //从歌词列表中获取，要用真实索引
            int realNumber = lineNumber - DEFAULT_FILL_LYRIC_COUNT;

            //获取当前时间是该行的的第几个字
            int lyricCurrentWordIndex = lyric.getWordIndex(realNumber, position);

            //获取当前时间的该字，已经播放的时间
            float wordPlayedTime = lyric.getWordPlayedTime(realNumber, position);

            //获取View要用添加填充Item的索引
            View view = layoutManager.findViewByPosition(lineNumber);

            if (view != null) {
                LyricLineView llv = view.findViewById(R.id.llv);

                llv.setLyricCurrentWordIndex(lyricCurrentWordIndex);
                llv.setWordPlayedTime(wordPlayedTime);
                llv.show(position);
            }
        }

    }

    private void scrollToPosition(int lineNumber) {
        adapter.setSelectedIndex(lineNumber);
        //rv.smoothScrollToPosition(lineNumber);

        //该方法会将指定item，滚动到顶部
        //offset是滚动到顶部后，在向下(+)偏移多少
        //如果我们想让一个Item在RecyclerView中间
        //那么偏移为RecyclerView.height/2
        //layoutManager.scrollToPositionWithOffset(lineNumber,548);

        //动态获取RecyclerView.height，兼容性更好
        if (lyricItemOffset > 0) {
            //大于0才滚动，因为前面我们添加了填充
            //所以默认第一行大概在中心位置
            layoutManager.scrollToPositionWithOffset(lineNumber,lyricItemOffset);
        }
    }

    private boolean isEmptyLyric() {
        return lyric==null;
    }

    @Override
    public void onGlobalLayout() {
        lyricItemOffset=rv.getHeight()/2;
    }

    //这样创建有内存泄漏，在性能优化我们具体讲解
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HIDE_TIME_LINE:
                    showScrollLyricView();
                    break;
            }
        }
    };

    /**
     * 显示歌词滚动效果
     */
    private void showScrollLyricView() {
        isDrag=false;
        ll_lyric_drag_container.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (onLyricClickListener != null) {
            onLyricClickListener.onLyricClick(scrollSelectedLyricLine.getStartTime());

            //马上显示歌词滚动
            showScrollLyricView();

            //取消定时器
            cancelTask();
        }
    }

    /**
     * 设置歌词监听器
     * @param lyricListener
     */
    public void setLyricListener(LyricListener lyricListener) {
        this.lyricListener = lyricListener;
    }

    @Override
    public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
        if (lyricListener != null) {
            lyricListener.onLyricItemClick(position);
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
        if (lyricListener != null) {
            lyricListener.onLyricItemLongClick(position);
        }
        return true;
    }

    /**
     * 歌词View监听器
     */
    public interface LyricListener{
        /**
         * 当前歌词点击回调
         * @param position
         */
        void onLyricItemClick(int position);

        /**
         * 当歌词长按回调
         * @param position
         */
        void onLyricItemLongClick(int position);
    }
}
