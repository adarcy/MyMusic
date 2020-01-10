package com.ixuea.courses.mymusic.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.CommentAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Comment;
import com.ixuea.courses.mymusic.domain.Video;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.DensityUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VideoDetailActivity extends BaseTitleActivity  implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "TAG";
    private RecyclerView rv;
    private CommentAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private VideoView vv;
    private AppBarLayout abl;
    private LinearLayout bottom_controller_container;
    private RelativeLayout video_container;
    private ImageView iv_video_play;
    private SeekBar sb_progress;
    private RelativeLayout video_touch_container;
    private String id;
    private int finalVideoHeight;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private ImageView iv_screen;
    private CountDownTimer countDownTimer;
    private ImageView iv_avatar;
    private TextView tv_nickname;
    private TextView tv_title;
    private TextView tv_created_at;
    private TextView tv_clicks_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
        finalVideoHeight = DensityUtil.dip2px(this, 200);

        abl = findViewById(R.id.abl);
        iv_video_play = findViewById(R.id.iv_video_play);
        sb_progress = findViewById(R.id.sb_progress);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        iv_screen = findViewById(R.id.iv_screen);
        video_container = findViewById(R.id.video_container);
        bottom_controller_container = findViewById(R.id.bottom_controller_container);
        video_touch_container = findViewById(R.id.video_touch_container);
        vv = findViewById(R.id.vv);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

        //vv.setMediaController(new MediaController(this));
        vv.requestFocus();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        id = getIntent().getStringExtra(Consts.ID);

        adapter = new CommentAdapter(getActivity());
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                Object data = adapter.getData(position);
                //if (data instanceof Comment) {
                //    showCommentMoreDialog((Comment) data);
                //}
            }
        });

        adapterWrapper = new LRecyclerViewAdapter(adapter);
        adapterWrapper.addHeaderView(createHeaderView());
        rv.setAdapter(adapterWrapper);

        fetchDetailData();
        fetchData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        video_touch_container.setOnClickListener(this);
        iv_video_play.setOnClickListener(this);
        sb_progress.setOnSeekBarChangeListener(this);
        iv_screen.setOnClickListener(this);
        vv.setOnPreparedListener(this);
        vv.setOnErrorListener(this);
    }

    private void fetchDetailData() {
        Api.getInstance().videoDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Video>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<Video> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    public void next(Video video) {
        vv.setVideoURI(Uri.parse(ImageUtil.getImageURI(video.getUri())));
        vv.start();

        setTitle(video.getTitle());

        ImageUtil.showCircle(getActivity(),iv_avatar,video.getUser().getAvatar());
        tv_title.setText(video.getTitle());
        tv_nickname.setText(video.getUser().getNickname());
        tv_created_at.setText(getResources().getString(R.string.video_created_at, TimeUtil.dateTimeFormat1(video.getCreated_at())));
        tv_clicks_count.setText(getResources().getString(R.string.video_clicks_count,video.getClicks_count()));
    }

    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_video_detail, (ViewGroup) rv.getParent(), false);
        iv_avatar = top.findViewById(R.id.iv_avatar);
        tv_nickname = top.findViewById(R.id.tv_nickname);
        tv_title = top.findViewById(R.id.tv_title);
        tv_created_at = top.findViewById(R.id.tv_created_at);
        tv_clicks_count = top.findViewById(R.id.tv_clicks_count);
        return top;
    }

    private void fetchData() {
        final ArrayList<Object> objects = new ArrayList<>();

        final HashMap<String, String> querys = new HashMap<>();
        querys.put(Consts.VIDEO_ID, id);
        Api.getInstance().comments(querys)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<Comment>>(getActivity()) {
                    @Override
                    public void onSucceeded(ListResponse<Comment> data) {
                        super.onSucceeded(data);
                        objects.addAll(data.getData());
                        adapter.setData(objects);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        vv.stopPlayback();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //重新计算视频的高，这样没有黑边，有更好的体验
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        int screenWidth = ScreenUtil.getScreenWidth(this);

        double scale = screenWidth * 1.0 / videoWidth;

        finalVideoHeight = (int) (videoHeight * scale);
        updatePlayerLayout();

        int duration = mp.getDuration();
        sb_progress.setMax(duration);
        tv_end_time.setText("/"+TimeUtil.formatMSTime(duration));

        startShowProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vv.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vv.pause();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: "+what+","+extra);
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerLayout();
    }

    private void updatePlayerLayout() {
        if (vv != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                //设置view的布局，宽高之类
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) video_container.getLayoutParams();
                layoutParams.height = finalVideoHeight;


            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                //设置view的布局，宽高
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) video_container.getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_touch_container:
                showOrHideController();
                break;
            case R.id.iv_video_play:
                playOrPause();
                break;
            case R.id.iv_screen:
                changeFullScreen();
                break;
        }
    }

    private void playOrPause() {
        if (vv.isPlaying()) {
            vv.pause();
            iv_video_play.setImageResource(R.drawable.ic_video_play);
            Log.d(TAG, "playOrPause: pause");
        } else {
            vv.start();
            iv_video_play.setImageResource(R.drawable.ic_video_pause);
            Log.d(TAG, "playOrPause: start");
        }
    }

    private void showOrHideController() {
        if (bottom_controller_container.getVisibility() == View.VISIBLE) {
            bottom_controller_container.setVisibility(View.GONE);
            abl.setVisibility(View.GONE);
            iv_video_play.setVisibility(View.GONE);
            stopShowProgress();
        } else {
            bottom_controller_container.setVisibility(View.VISIBLE);
            abl.setVisibility(View.VISIBLE);
            iv_video_play.setVisibility(View.VISIBLE);
            startShowProgress();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        vv.seekTo(seekBar.getProgress());
        if (!vv.isPlaying()) {
            vv.start();
            Log.d(TAG, "onStopTrackingTouch:start");
        }
    }

    private void startShowProgress() {
        cancelTask();
        //倒计时的总时间,间隔
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                showProgress(vv.getCurrentPosition());
            }

            @Override
            public void onFinish() {
                showOrHideController();
            }
        };

        countDownTimer.start();
    }


    private void stopShowProgress() {
        cancelTask();
    }

    private void cancelTask() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void showProgress(int progress) {
        tv_start_time.setText(TimeUtil.formatMSTime((int) progress));
        sb_progress.setProgress((int) progress);

    }

    protected void changeFullScreen(){
        if(isFullScreen()){
            //竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen()) {
            changeFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return false;
    }
}
