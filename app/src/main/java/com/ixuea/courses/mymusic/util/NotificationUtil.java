package com.ixuea.courses.mymusic.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.manager.impl.UserManagerImpl;

/**
 * Created by smile on 2018/6/3.
 */

public class NotificationUtil {
    private static final int NOTIFICATION_MUSIC_ID = 10000;
    private static final int NOTIFICATION_UNLOCK_LYRIC_ID = 10001;
    private static NotificationManager notificationManager;

    /**
     * 显示播放音乐通知栏
     *
     * @param context
     * @param song
     * @param isPlaying
     */
    public static void showMusicNotification(final Context context, final Song song, final boolean isPlaying) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context).asBitmap().load(ImageUtil.getImageURI(song.getBanner())).apply(options).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                //这个布局的根View的尺寸不能引用dimen文件，要写死
                //设置标准通知数据
                RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_music_play);
                contentView.setImageViewBitmap(R.id.iv_icon, resource);
                contentView.setTextViewText(R.id.tv_title, song.getTitle());
                contentView.setTextViewText(R.id.tv_info, String.format("%s - %s", song.getArtist_name(), song.getAlbum_title()));
                int playResId = isPlaying ? R.drawable.ic_music_notification_pause : R.drawable.ic_music_notification_play;
                contentView.setImageViewResource(R.id.iv_play, playResId);

                //设置标准通知，点击事件
                PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_PLAY.hashCode(), new Intent(Consts.ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
                contentView.setOnClickPendingIntent(R.id.iv_play, playPendingIntent);
                PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_NEXT.hashCode(), new Intent(Consts.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
                contentView.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);
                PendingIntent lyricPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_LYRIC.hashCode(), new Intent(Consts.ACTION_LYRIC), PendingIntent.FLAG_UPDATE_CURRENT);
                contentView.setOnClickPendingIntent(R.id.iv_play_screen_lyric, lyricPendingIntent);

                //设置可展开通知数据
                RemoteViews contentBigView = new RemoteViews(context.getPackageName(), R.layout.notification_music_play_large);
                contentBigView.setImageViewBitmap(R.id.iv_icon, resource);
                contentBigView.setTextViewText(R.id.tv_title, song.getTitle());
                contentBigView.setTextViewText(R.id.tv_info, String.format("%s - %s", song.getArtist_name(), song.getAlbum_title()));
                contentBigView.setImageViewResource(R.id.iv_play, playResId);

                //设置可展开通知，点击事件
                contentBigView.setOnClickPendingIntent(R.id.iv_like, PendingIntent.getBroadcast(context, Consts.ACTION_LIKE.hashCode(), new Intent(Consts.ACTION_LIKE), PendingIntent.FLAG_UPDATE_CURRENT));
                contentBigView.setOnClickPendingIntent(R.id.iv_previous, PendingIntent.getBroadcast(context, Consts.ACTION_PREVIOUS.hashCode(), new Intent(Consts.ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT));
                contentBigView.setOnClickPendingIntent(R.id.iv_play, playPendingIntent);
                contentBigView.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);
                contentBigView.setOnClickPendingIntent(R.id.iv_play_screen_lyric, lyricPendingIntent);


                Intent intent = new Intent(context, MainActivity.class);
                intent.setAction(Consts.ACTION_MUSIC_PLAYER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentPendingIntent = PendingIntent.getActivity(context, Consts.ACTION_LYRIC.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setCustomContentView(contentView)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo))
                        .setCustomBigContentView(contentBigView)
                        .setContentIntent(contentPendingIntent);

                NotificationUtil.notify(context, NOTIFICATION_MUSIC_ID, builder.build());

            }
        });

    }

    /**
     * 显示解锁桌面歌词通知栏
     *
     * @param context
     */
    public static void showUnlockLyricNotification(final Context context) {
        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_UNLOCK_LYRIC.hashCode(), new Intent(Consts.ACTION_UNLOCK_LYRIC), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("桌面歌词已经锁定")
                .setContentText("点击解锁")
                .setContentIntent(contentPendingIntent);

        notify(context, NOTIFICATION_UNLOCK_LYRIC_ID, builder.build());
    }


    /**
     * 如果歌词解锁了，需要清除解锁桌面歌词通知栏
     *
     * @param context
     */
    public static void clearUnlockLyricNotification(Context context) {
        getNotificationManager(context);
        notificationManager.cancel(NOTIFICATION_UNLOCK_LYRIC_ID);
    }

    /**
     * 显示聊天消息（不再聊天界面才显示）
     *
     * @param context
     * @param userId
     * @param content
     */
    public static void showMessageNotification(final Context context, final String userId, final String content, final int unreadCount) {
        UserManager userManager = UserManagerImpl.getInstance(context);
        //获取消息发送者的的信息
        userManager.getUser(userId, new UserManager.OnUserListener() {
            @Override
            public void onUser(final User user) {
                //获取用户信息，获取用户头像的Bitmap
                //这里的嵌套可以通过RxJava转换成链式
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(context).asBitmap().load(user.getAvatar()).apply(options).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_message);
                        contentView.setImageViewBitmap(R.id.iv_icon, resource);

                        if (unreadCount > 1) {
                            contentView.setTextViewText(R.id.tv_title, String.format("%s（%d条消息）",user.getNickname(),unreadCount));
                        } else {
                            contentView.setTextViewText(R.id.tv_title, user.getNickname());
                        }

                        contentView.setTextViewText(R.id.tv_info, content);

                        //点击通知，跳转到聊天界面
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(Consts.ID, userId);
                        intent.setAction(Consts.ACTION_MESSAGE);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, Consts.ACTION_LYRIC.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_logo)
                                .setCustomContentView(contentView)
                                .setAutoCancel(true)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo))
                                .setContentIntent(contentPendingIntent);

                        //用发送者的id来显示通知
                        NotificationUtil.notify(context, userId.hashCode(), builder.build());


                    }
                });
            }
        });


    }

    private static void notify(Context context, int id, Notification notification) {
        getNotificationManager(context);
        notificationManager.notify(id, notification);
    }

    private static void getNotificationManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }
}
