package com.ixuea.courses.mymusic.util;

import android.content.Context;

import com.ixuea.courses.mymusic.domain.SearchHistory;
import com.ixuea.courses.mymusic.domain.Song;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smile on 06/03/2018.
 */

public class OrmUtil {
    static LiteOrm orm;
    private static OrmUtil instance;

    public OrmUtil(Context context) {
        orm = LiteOrm.newSingleInstance(context, "ixuea-music.db");
    }

    public static OrmUtil getInstance(Context context) {
        if (instance == null) {
            instance = new OrmUtil(context);
        }
        return instance;
    }

    public void saveSong(Song song, String userId) {
        song.setUserId(userId);
        orm.save(song);
    }

    public void deleteSongs(String userId) {
        orm.delete(new WhereBuilder(Song.class)
                .where("userId=?", new String[]{userId}));
    }

    public List<Song> queryPlayList(String userId) {
        ArrayList<Song> songs = orm
                .query(new QueryBuilder<Song>(Song.class)
                        .whereEquals("userId", userId)
                        .whereAppendAnd()
                        .whereEquals("playList", true)
                        .appendOrderAscBy("id"));

        return songs;
    }

    public void deleteSong(Song song) {
        orm.delete(song);
    }

    public List<Song> queryLocalMusic(String userId, String orderBy) {
        ArrayList<Song> songs = orm
                .query(new QueryBuilder<Song>(Song.class)
                        .whereEquals("userId", userId)
                        .whereAppendAnd()
                        .whereEquals("source", Song.SOURCE_LOCAL)
                        .appendOrderAscBy(orderBy));

        return songs;
    }

    public int countOfLocalMusic(String userId) {
        return (int) orm.queryCount(new QueryBuilder<Song>(Song.class)
                .whereEquals("userId", userId)
                .whereAppendAnd()
                .whereEquals("source", Song.SOURCE_LOCAL));

    }

    public Song findSongById(String id) {
        return orm.queryById(id,Song.class);
    }

    public List<SearchHistory> queryAllSearchHistory() {
        return orm.query(new QueryBuilder<SearchHistory>(SearchHistory.class).appendOrderDescBy("created_at"));
    }

    public void createOrUpdate(SearchHistory searchHistory) {
        orm.save(searchHistory);
    }

    public void deleteSearchHistory(SearchHistory data) {
        orm.delete(data);
    }


}
