package com.ixuea.courses.mymusic.activity;

import android.animation.ValueAnimator;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.event.ScanMusicCompleteEvent;
import com.ixuea.courses.mymusic.util.MusicUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ScanLocalMusicActivity extends BaseTitleActivity implements View.OnClickListener {

    private static final String SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + " !=0 AND " + MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " + MediaStore.Audio.AudioColumns.DURATION + " >= ?";
    private static final double DEFAULT_RADIUS = 30;

    /**
     * 1M
     */
    private static final int DEFAULT_FILTER_MUSIC_SIZE = 1*1024*1024;

    /**
     * 60S
     */
    private static final int DEFAULT_FILTER_MUSIC_TIME = 60*1000;

    private ImageView iv_scan_music_zoom;
    private ImageView iv_scan_music_line;
    private TextView tv_progress;
    private Button bt_scan_music;

    private boolean isScanning;

    private ValueAnimator zoomValueAnimator;
    private TranslateAnimation lineAnimation;

    private boolean isScanComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_local_music);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        iv_scan_music_zoom=findViewById(R.id.iv_scan_music_zoom);
        iv_scan_music_line=findViewById(R.id.iv_scan_music_line);
        tv_progress=findViewById(R.id.tv_progress);
        bt_scan_music=findViewById(R.id.bt_scan_music);
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_scan_music.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isScanComplete) {
            EventBus.getDefault().post(new ScanMusicCompleteEvent());
            finish();
            return;
        }

        if (isScanning) {
            isScanning=false;
            bt_scan_music.setText(R.string.start_scan);
            bt_scan_music.setBackgroundResource(R.drawable.selector_button_reverse);
            bt_scan_music.setTextColor(getResources().getColorStateList(R.drawable.selector_text_reverse));
            stopScan();
        } else {
            isScanning=true;
            bt_scan_music.setText(R.string.stop_scan);
            bt_scan_music.setBackgroundResource(R.drawable.selector_button);
            bt_scan_music.setTextColor(getResources().getColorStateList(R.drawable.selector_text));
            startScan();
        }
    }

    private void stopScan() {
        iv_scan_music_line.clearAnimation();
        iv_scan_music_line.setVisibility(View.GONE);
        if (null != zoomValueAnimator) {
            zoomValueAnimator.cancel();
            zoomValueAnimator=null;
        }
    }

    private void startScan() {
        //放大镜搜索效果
        zoomValueAnimator = ValueAnimator.ofFloat(0.0F, 360.0F);
        zoomValueAnimator.setInterpolator(new LinearInterpolator());
        zoomValueAnimator.setDuration(30000);
        zoomValueAnimator.setRepeatCount(-1);
        zoomValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        zoomValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                float translateX = (float) (DEFAULT_RADIUS * Math.cos(angle));
                float translateY = (float) (DEFAULT_RADIUS * Math.sin(angle));
                iv_scan_music_zoom.setTranslationX(translateX);
                iv_scan_music_zoom.setTranslationY(translateY);
            }
        });
        zoomValueAnimator.start();

        //扫描线，不使用属性动画是因为属性动画要获取坐标
        lineAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.7f);
        lineAnimation.setInterpolator(new DecelerateInterpolator());
        lineAnimation.setDuration(2000);
        lineAnimation.setRepeatCount(-1);
        lineAnimation.setRepeatMode(TranslateAnimation.RESTART);
        lineAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_scan_music_line.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_scan_music_line.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        iv_scan_music_line.clearAnimation();
        iv_scan_music_line.startAnimation(lineAnimation);

        startScanMusic();
    }

    /**
     * 扫描音乐，我们这里只扫描媒体库，不全盘扫描
     */
    private void startScanMusic() {
        new AsyncTask<Void, String, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Void... voids) {
                List<Song> songs = null;
                try {

                    SystemClock.sleep(5000);

                    songs = new ArrayList<>();

                    /**
                     * 使用内容提供者查询
                     * 我们这里是查询音乐大小大于1M，时间大于60秒
                     *
                     * @param uri 资源标识符
                     * @param projection 选择那些字段
                     * @param selection 条件
                     * @param selectionArgs 条件参数
                     * @param sortOrder 排序
                     */
                    Cursor cursor = getContentResolver().query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String[]{
                                    BaseColumns._ID,
                                    MediaStore.Audio.AudioColumns.TITLE,
                                    MediaStore.Audio.AudioColumns.ARTIST,
                                    MediaStore.Audio.AudioColumns.ALBUM,
                                    MediaStore.Audio.AudioColumns.ALBUM_ID,
                                    MediaStore.Audio.AudioColumns.DATA,
                                    MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                                    MediaStore.Audio.AudioColumns.SIZE,
                                    MediaStore.Audio.AudioColumns.DURATION
                            },
                            SELECTION,
                            new String[]{
                                    String.valueOf(DEFAULT_FILTER_MUSIC_SIZE),
                                    String.valueOf(DEFAULT_FILTER_MUSIC_TIME)
                            },
                            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                    while (cursor != null && cursor.moveToNext()) {
                        //如果有值就遍历每一行
                        long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                        String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                        String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                        long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                        //String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                        //long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                        Song song = new Song();
                        song.setId(String.valueOf(id));
                        song.setTitle(title);
                        song.setArtist_name(artist);
                        song.setAlbum_id(String.valueOf(albumId));
                        song.setAlbum_title(album);
                        song.setAlbum_banner(MusicUtil.getAlbumBanner(getActivity(),String.valueOf(albumId)));
                        song.setDuration(duration);
                        song.setUri(path);
                        song.setSource(Song.SOURCE_LOCAL);
                        songs.add(song);

                        //保存到数据库
                        orm.saveSong(song,sp.getUserId());

                        publishProgress(path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {

                }

                return songs;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                tv_progress.setText(values[0]);
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);
                isScanComplete =true;
                stopScan();
                tv_progress.setText(getResources().getString(R.string.find_music_count,songs.size()));
                bt_scan_music.setBackgroundResource(R.drawable.selector_button_reverse);
                bt_scan_music.setTextColor(getResources().getColorStateList(R.drawable.selector_text_reverse));
                bt_scan_music.setText(R.string.go_my_music);
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        stopScan();
        super.onBackPressed();
    }

}
