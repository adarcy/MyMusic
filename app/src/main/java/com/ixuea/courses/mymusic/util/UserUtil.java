package com.ixuea.courses.mymusic.util;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.User;


/**
 * Created by smile on 05/03/2018.
 */

public class UserUtil {
    public static void showUser(Activity activity, User data, ImageView iv_user_avatar, TextView tv_user_nickname, TextView tv_user_description) {
        if (data == null) {
            ImageUtil.showCircle(activity, iv_user_avatar, R.drawable.default_avatar);
        } else {
            ImageUtil.showCircle(activity, iv_user_avatar, data.getAvatar());
            tv_user_nickname.setText(data.getNickname());
            tv_user_description.setText(data.getDescription());

        }

    }

    public static void showNotLoginUser(Activity activity,  ImageView iv_user_avatar, TextView tv_user_nickname, TextView tv_user_description) {
        ImageUtil.showCircle(activity, iv_user_avatar, R.drawable.default_avatar);
        tv_user_nickname.setText("请先登陆");
        tv_user_description.setText("");


    }
}
