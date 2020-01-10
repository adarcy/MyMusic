package com.ixuea.courses.mymusic.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ren on 2015/7/10 0010.
 */
public class PackageUtil {

    private static final String SCHEME = "package";

    private static Map<String, Drawable> appIconCache = new HashMap<String, Drawable>();

    public static Drawable getIcon(String packageName, PackageManager packageManger) {

        Drawable drawable = appIconCache.get(packageName);
        if (drawable != null) {
            return drawable;
        }
        try {
            Drawable applicationIcon = packageManger.getApplicationIcon(packageName);
            appIconCache.put(packageName, applicationIcon);
            return applicationIcon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据Pid获取当前进程的名字，一般就是当前app的包名
     *
     * @param context 上下文
     * @param pid 进程的id
     * @return 返回进程的名字
     */
    public static String getAppName(Context context, int pid)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try
            {
                if (info.pid == pid)
                {
                    // 根据进程的信息获取当前进程的名字
                    return info.processName;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }


}
