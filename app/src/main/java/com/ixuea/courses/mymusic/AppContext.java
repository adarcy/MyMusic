package com.ixuea.courses.mymusic;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.util.Log;

import com.ixuea.courses.mymusic.domain.event.OnMessageEvent;
import com.ixuea.courses.mymusic.util.LogUtil;
import com.ixuea.courses.mymusic.util.MessageUtil;
import com.ixuea.courses.mymusic.util.NotificationUtil;
import com.ixuea.courses.mymusic.util.PackageUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;
import com.mob.MobSDK;

import org.greenrobot.eventbus.EventBus;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by smile on 2018/6/21.
 */

public class AppContext extends Application {
    private static final String TAG = "AppContext";
    private static Context context;
    private static RongIMClient imClient;
    private static SharedPreferencesUtil sp;

    public static Context getContext() {
        return context;
    }

    public void onCreate() {
        super.onCreate();
        sp = SharedPreferencesUtil.getInstance(getApplicationContext());

        this.context = getApplicationContext();

        //初始化emoji
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        //Share SDK
        MobSDK.init(this);

        //    Rong IM
        RongIMClient.init(this);

        if (sp.isLogin()) {
            imConnect();
        }

        //当前版本的SDK会导致引用崩溃后挂起，不打印日志，可以先设置如下代码让能打印崩溃日志，挂起问题他们后面会处理
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void imConnect() {
        String appName = PackageUtil.getAppName(getApplicationContext(), Process.myPid());
        String packageName = getApplicationInfo().packageName;
        if (packageName.equals(appName)) {
            LogUtil.d(TAG,"rong connect"+appName+","+packageName+","+(Looper.myLooper() == Looper.getMainLooper()));
            //if (Looper.myLooper() == Looper.getMainLooper()) {
            //由于使用的多进程，所以会调用多次，该初始化要主线程
            imClient = RongIMClient.connect(sp.getIMToken(), new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    Log.d(TAG, "im onTokenIncorrect: ");
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "im onSuccess: " + s);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d(TAG, "im onError: " + errorCode);
                }
            });

            imClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                @Override
                public boolean onReceived(Message message, int i) {
                    //该方法的调用不再主线程
                    Log.d(TAG, "im onReceived: " + message + "," + i+","+(Looper.myLooper() == Looper.getMainLooper()));

                    if (EventBus.getDefault().hasSubscriberForEvent(OnMessageEvent.class)) {
                        //如果有监听该事件，表示在聊天界面
                        EventBus.getDefault().post(new OnMessageEvent(message));
                    } else {
                        handler.obtainMessage(0,message).sendToTarget();
                    }

                    return false;
                }
            });
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void logout() {
        imClient.logout();
    }

    public static RongIMClient getImClient() {
        return imClient;
    }

    private Handler handler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //如果没有就显示通知
            final Message message= (Message) msg.obj;
            imClient.getUnreadCount(Conversation.ConversationType.PRIVATE, message.getSenderUserId(), new RongIMClient.ResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    NotificationUtil.showMessageNotification(context.getApplicationContext(),message.getSenderUserId(), MessageUtil.getContent(message.getContent()),integer);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });

        }
    };
}
