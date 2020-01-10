package com.ixuea.courses.mymusic.util;

import com.ixuea.courses.mymusic.domain.Song;

import java.util.Collection;
import java.util.List;


/**
 * Created by smile on 2018/6/6.
 */

public class DataUtil {
    /**
     * 将音乐中，对象的字段，展开
     *
     * @param songs
     * @return
     */
    public static List<Song> fill(List<Song> songs) {
        for (Song s:songs
             ) {
            s.fill();
        }
        return songs;
    }

    /**
     * 更改是否在播放类别表示
     * @return
     */
    public static Collection<? extends Song> changePlayListFlag(List<Song> songs, boolean isPlayList) {
        for (Song s:songs
                ) {
            s.setPlayList(isPlayList);
        }
        return songs;
    }
}
