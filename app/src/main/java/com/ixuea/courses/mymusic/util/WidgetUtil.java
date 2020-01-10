package com.ixuea.courses.mymusic.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.receiver.Widget41;

/**
 * Created by smile on 2018/6/12.
 */

public class WidgetUtil {
    public static void onPlaying(Context context) {
        RemoteViews remoteViews = getWidget41RemoteViews(context);

        remoteViews.setImageViewResource(R.id.iv_play, R.drawable.ic_music_pause);

        update(context,remoteViews);
    }


    public static void onPaused(Context context) {
        RemoteViews remoteViews = getWidget41RemoteViews(context);

        remoteViews.setImageViewResource(R.id.iv_play, R.drawable.ic_music_play);

        update(context,remoteViews);
    }

    public static void onPrepared(final Context context, Song song) {
        final RemoteViews remoteViews = getWidget41RemoteViews(context);

        remoteViews.setTextViewText(R.id.tv_title, String.format("%s - %s", song.getTitle(), song.getArtist_name()));
        remoteViews.setProgressBar(R.id.pb, (int) song.getDuration(),0,false);

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context).asBitmap().load(ImageUtil.getImageURI(song.getBanner())).apply(options).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                remoteViews.setImageViewBitmap(R.id.iv_icon, resource);
                update(context,remoteViews);

            }
        });
    }

    public static void onProgress(Context context,long progress, long total) {
        RemoteViews remoteViews = getWidget41RemoteViews(context);

        remoteViews.setProgressBar(R.id.pb, (int)total,(int)progress,false);

        update(context,remoteViews);
    }

    private static void update(Context context,RemoteViews remoteViews) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context,Widget41.class);
        manager.updateAppWidget(componentName,remoteViews);
    }

    private static RemoteViews getWidget41RemoteViews(Context context) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_41);
        return remoteViews;
    }
}
