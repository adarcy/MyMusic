package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by smile on 02/03/2018.
 */

public class SharedPreferencesUtil {
    public static final String TAG = "SharedPreferencesUtil";
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String USER_IM_TOKEN = "USER_IM_TOKEN";
    private static final String USER_ID = "USER_ID";
    private static final String FIRST = "FIRST";
    private static final String KEY_LYRIC_LOCK = "KEY_LYRIC_LOCK";
    private static final String KEY_LYRIC_Y = "KEY_LYRIC_Y";
    private static final String KEY_LYRIC_FONT_SIZE = "KEY_LYRIC_FONT_SIZE";
    private static final String KEY_LYRIC_TEXT_COLOR = "KEY_LYRIC_TEXT_COLOR";
    private static final String KEY_SHOW_LYRIC = "KEY_SHOW_LYRIC";
    private static final String KEY_LOCAL_MUSIC_SORT_KEY = "KEY_LOCAL_MUSIC_SORT_KEY";
    private static final String CURRENT_PLAY_SONG_ID = "CURRENT_PLAY_SONG_ID";
    private static final String LAST_PLAY_SONG_PROGRESS = "LAST_PLAY_SONG_PROGRESS";
    private static final String DEFAULT_LOCAL_MUSIC_SORT_KEY = "id";

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SharedPreferencesUtil mSharedPreferencesUtil;
    private final Context context;

    public SharedPreferencesUtil(Context context) {

        this.context = context.getApplicationContext();
        mPreferences =   this.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (mSharedPreferencesUtil ==null){
            mSharedPreferencesUtil =new SharedPreferencesUtil(context);
        }
        return  mSharedPreferencesUtil;
    }

    public static SharedPreferencesUtil getCurrentInstance() {
        return  mSharedPreferencesUtil;
    }

    public void put(String key, String value) {
        mEditor.putString(key,value);
        mEditor.commit();
    }

    public void putBoolean(String key,boolean value) {
        mEditor.putBoolean(key,value);
        mEditor.commit();
    }

    public String get(String key) {
        return mPreferences.getString(key,"");
    }

    public boolean getBoolean(String key,boolean defaultValue) {
        return mPreferences.getBoolean(key,defaultValue);
    }

    public void removeSP(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    //    自用方法
    public  void setToken(String token) {
        put(USER_TOKEN,token);
    }

    public String getToken() {
        return get(USER_TOKEN);
    }

    public String getIMToken() {
        return get(USER_IM_TOKEN);
    }

    public  void setIMToken(String token) {
        put(USER_IM_TOKEN,token);
    }


    public boolean isLogin() {
        return !TextUtils.isEmpty(get(USER_TOKEN));
    }

    public boolean isFirst() {
        return getBoolean(FIRST,true);
    }

    public void setFirst(boolean value) {
        putBoolean(FIRST,value);
    }

    public String getUserId() {
        return get(USER_ID);
    }

    public void setUserId(String userId) {
        put(USER_ID,userId);
    }

    public void logout() {
        put(USER_TOKEN,"");
        put(USER_IM_TOKEN,"");
        put(USER_ID,"");
    }

    public String getLastPlaySongId() {
        return get(CURRENT_PLAY_SONG_ID);
    }

    public void setLastPlaySongId(String id) {
        put(CURRENT_PLAY_SONG_ID,id);
    }

    public void setLastSongProgress(int progress) {
        putInt(LAST_PLAY_SONG_PROGRESS,progress);
    }

    private void putInt(String key, int value) {
        mEditor.putInt(key,value);
        mEditor.apply();
    }

    public int getLastSongProgress() {
        return getInt(LAST_PLAY_SONG_PROGRESS,0);
    }

    private int getInt(String key,int defaultValue) {
        return mPreferences.getInt(key,defaultValue);
    }

    public int getGlobalLyricY() {
        return getInt(KEY_LYRIC_Y,0);
    }

    public void setGlobalLyricY(int y) {
        putInt(KEY_LYRIC_Y,y);
    }

    public boolean isLyricLock() {
        return getBoolean(KEY_LYRIC_LOCK,false);
    }

    public void setLyricLock(boolean value) {
        putBoolean(KEY_LYRIC_LOCK,value);
    }

    public boolean isShowLyric() {
        return getBoolean(KEY_SHOW_LYRIC,false);
    }

    public void setShowLyric(boolean value) {
        putBoolean(KEY_SHOW_LYRIC,value);
    }

    public void setGlobalLyricFontSize(int size) {
        putInt(KEY_LYRIC_FONT_SIZE,size);
    }

    public int getGlobalLyricFontSize() {
        //默认18DP
        return getInt(KEY_LYRIC_FONT_SIZE,DensityUtil.dip2px(this.context, 18));
    }

    public void setGlobalLyricTextColorIndex(int index) {
        putInt(KEY_LYRIC_TEXT_COLOR,index);
    }

    public int getGlobalLyricTextColorIndex() {
        return getInt(KEY_LYRIC_TEXT_COLOR,0);
    }

    /**
     * 本地歌曲排序，默认id排序
     * @return
     */
    public int getLocalMusicSortKey() {
        return getInt(KEY_LOCAL_MUSIC_SORT_KEY,0);
    }

    public void setLocalMusicSortKey(int sortIndex) {
        putInt(KEY_LOCAL_MUSIC_SORT_KEY,sortIndex);

    }
}
