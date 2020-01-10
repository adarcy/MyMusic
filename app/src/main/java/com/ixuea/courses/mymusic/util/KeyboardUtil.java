package com.ixuea.courses.mymusic.util;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by smile on 2018/6/9.
 */

public class KeyboardUtil {
    public static void hideKeyboard(Activity activity) {
        if(activity.getCurrentFocus()!=null)
        {
            ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
