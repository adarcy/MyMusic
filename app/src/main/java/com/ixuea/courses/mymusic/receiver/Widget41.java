package com.ixuea.courses.mymusic.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.manager.PlayListManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;


/**
 * Created by smile on 2018/6/12.
 */

public class Widget41 extends AppWidgetProvider {


    private static final String TAG = "Widget41";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled: ");

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "onDisabled: ");
    }

    /**
     * 添加，重新运行应用，周期时间，都会调用
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate: ");

        PlayListManager playList = MusicPlayerService.getPlayListManager(context.getApplicationContext());

        final Song song = playList.getPlayData();

        final int N = appWidgetIds.length;
        SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(context);

        for (int i=0; i<N; i++) {
            final int appWidgetId = appWidgetIds[i];

            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_PREVIOUS.hashCode(), new Intent(Consts.ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_PLAY.hashCode(), new Intent(Consts.ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, Consts.ACTION_NEXT.hashCode(), new Intent(Consts.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);

            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_41);
            remoteViews.setOnClickPendingIntent(R.id.iv_play, playPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.iv_previous, previousPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);

            if (song == null) {
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } else {
                remoteViews.setTextViewText(R.id.tv_title, String.format("%s - %s", song.getTitle(), song.getArtist_name()));
                remoteViews.setProgressBar(R.id.pb, (int) song.getDuration(),sp.getLastSongProgress(),false);

                RequestOptions options = new RequestOptions();
                options.centerCrop();
                Glide.with(context).asBitmap().load(ImageUtil.getImageURI(song.getBanner())).apply(options).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        remoteViews.setImageViewBitmap(R.id.iv_icon, resource);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                });
            }

        }
    }


}
