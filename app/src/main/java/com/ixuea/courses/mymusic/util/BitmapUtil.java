package com.ixuea.courses.mymusic.util;

import android.graphics.Bitmap;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by smile on 2018/6/5.
 */

public class BitmapUtil {
    public static void saveToFile(Bitmap bitmap, File file) {
        FileOutputStream out=null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            //使用了apache的common io工具包
            IOUtils.closeQuietly(out);
        }
    }

}
