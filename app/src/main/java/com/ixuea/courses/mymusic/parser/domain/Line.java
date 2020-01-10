package com.ixuea.courses.mymusic.parser.domain;

import com.ixuea.courses.mymusic.domain.Base;

/**
 * Created by smile on 2018/5/30.
 */

public class Line extends Base {
    /**
     * 整行歌词
     */
    private String lineLyrics = null;

    /**
     * 开始时间
     */
    private long startTime = 0;

    /**
     * 每一个字
     */
    public String[] lyricsWord = null;

    /**
     * 每一个字所定义的时间
     */
    public int[] wordDuration = null;

    /**
     * 结束时间
     */
    private long endTime = 0;

    public String[] getLyricsWord() {
        return lyricsWord;
    }

    public void setLyricsWord(String[] lyricsWord) {
        this.lyricsWord = lyricsWord;
    }

    public int[] getWordDuration() {
        return wordDuration;
    }

    public void setWordDuration(int[] wordDuration) {
        this.wordDuration = wordDuration;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getLineLyrics() {
        return lineLyrics;
    }

    public void setLineLyrics(String lineLyrics) {
        this.lineLyrics = lineLyrics;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
