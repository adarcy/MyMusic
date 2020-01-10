package com.ixuea.courses.mymusic.parser.domain;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by smile on 2018/5/30.
 */

public class Lyric {
    public static final int TYPE_LRC = 0;
    public static final int TYPE_KSC = 10;

    protected Map<String, Object> tags;
    protected TreeMap<Integer, Line> lyrics;

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public TreeMap<Integer, Line> getLyrics() {
        return lyrics;
    }

    public void setLyrics(TreeMap<Integer, Line> lyrics) {
        this.lyrics = lyrics;
    }

    /**
     * 获取当前播放进度所对应的行数
     *
     * @param position
     * @return
     */
    public int getLineNumber(long position) {
        for (int i = lyrics.size()-1; i >=0; i--) {
            //当前时间正好在改行的开始时间内。就是该行
            Line line = lyrics.get(i);
            if (position>=line.getStartTime()) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 根据时间，位置获取是第几个字
     *
     * @param lineNumber
     * @param position
     * @return
     */
    public int getWordIndex(int lineNumber, long position) {
        return -1;
    }

    /**
     * 获取当前行歌词已经播放的时间
     *
     * @param lineNumber
     * @param position
     * @return
     */
    public float getWordPlayedTime(int lineNumber, long position) {
        return -1;
    }

    /**
     * 歌词是否精确到字
     *
     * @return
     */
    public boolean isAccurate(){
        return false;
    }
}
