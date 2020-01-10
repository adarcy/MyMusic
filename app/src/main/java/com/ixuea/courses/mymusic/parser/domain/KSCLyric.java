package com.ixuea.courses.mymusic.parser.domain;

/**
 * Created by smile on 2018/5/30.
 */

public class KSCLyric extends Lyric {
    /**
     * 获取当前播放进度所对应的行数
     */
    @Override
    public int getLineNumber(long position) {
        for (int i = 0; i < lyrics.size(); i++) {
            //当前时间正好在改行的开始和结束时间内。就是该行
            Line line = (Line) lyrics.get(i);
            if (position >= lyrics.get(i).getStartTime() && position <= line.getEndTime()) {
                return i;
            }

            //当前时间在改行结束，但在下一行开始时间内。还是改行
            if (position > line.getEndTime() && i + 1 < lyrics.size() && position < lyrics
                    .get(i + 1).getStartTime()) {
                return i;
            }
        }

        //如果当前时间在最后一行的外。则还是最后一行
        Line line = (Line) lyrics.get(lyrics.size() - 1);
        if (position >= line.getEndTime()) {
            return lyrics.size() - 1;
        }

        return 0;
    }

    /**
     * 根据时间，位置获取是第几个字
     */
    @Override
    public int getWordIndex(int lineNumber, long position) {
        if (lineNumber == -1) {
            return -1;
        }
        Line lyric = (Line) lyrics.get(lineNumber);
        long startTime = lyric.getStartTime();
        for (int i = 0; i < lyric.getLyricsWord().length; i++) {
            startTime += lyric.getWordDuration()[i];
            if (position < startTime) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public float getWordPlayedTime(int lineNumber, long position) {
        if (lineNumber == -1) {
            return -1;
        }
        Line line = (Line) lyrics.get(lineNumber);
        long startTime = line.getStartTime();
        for (int i = 0; i < line.getLyricsWord().length; i++) {
            startTime += line.getWordDuration()[i];
            if (position < startTime) {
                return line.getWordDuration()[i] - (startTime - position);
            }
        }

        return -1;
    }

    public boolean isAccurate() {
        return true;
    }
}
