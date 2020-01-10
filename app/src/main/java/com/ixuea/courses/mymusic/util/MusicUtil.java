package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by smile on 2018/6/6.
 */

public class MusicUtil {
    public static String getAlbumBanner(Context context,String album_id) {
        Cursor cur =null;
        try {
            //String mUriAlbums = "content://media/external/audio/albums";
            String[] projection = new String[] { "album_art" };
            cur = context.getContentResolver().query(
                    Uri.parse(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI + "/" + album_id),
                    projection, null, null, null);
            if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                cur.moveToNext();
                return cur.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur!=null) {
                cur.close();
                cur = null;
            }

        }
        return null;
    }
}
