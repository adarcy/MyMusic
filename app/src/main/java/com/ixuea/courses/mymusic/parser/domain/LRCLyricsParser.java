package com.ixuea.courses.mymusic.parser.domain;

import com.ixuea.courses.mymusic.parser.LyricsParser;
import com.ixuea.courses.mymusic.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by smile on 2018/5/30.
 */

public class LRCLyricsParser extends LyricsParser {

    public LRCLyricsParser(int type, String content) {
        super(type, content);
    }

    @Override
    public void parse() {
        String[] strings = content.split("\n");

        lyric = new Lyric();

        TreeMap<Integer, Line> lyrics = new TreeMap<>();
        Map<String, Object> tags = new HashMap<>();

        String lineInfo=null;
        int lineNumber = 0;
        for (int i = 0; i < strings.length; i++) {
            try {
                lineInfo=strings[i];
                Line line = parserLine(tags, lineInfo);
                if (line != null) {
                    lyrics.put(lineNumber, line);
                    lineNumber++;
                }
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        lyric.setLyrics(lyrics);
        lyric.setTags(tags);
    }

    /**
     * 解析每一行歌词
     */
    private Line parserLine(Map<String, Object> tags, String lineInfo) {
        if (lineInfo.startsWith("[0")) {
            //歌词开始了
            Line line = new Line();

            int leftIndex = 1;
            int rightIndex = lineInfo.length();
            String[] lineComments = lineInfo.substring(leftIndex, rightIndex).split("]", -1);

            //开始时间
            String startTimeStr = lineComments[0];
            int startTime = TimeUtil.parseInteger(startTimeStr);
            line.setStartTime(startTime);

            //歌词
            String lineLyricsStr = lineComments[1];
            line.setLineLyrics(lineLyricsStr);

            return line;
        }

        return null;
    }
}
