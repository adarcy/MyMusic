package com.ixuea.courses.mymusic.util;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by smile on 2018/6/5.
 */

public class ViewUtil {
    public static Bitmap createBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();       //启用DrawingCache并创建位图

        //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);//禁用DrawingCahce否则会影响性能
        return bitmap;
    }
}
