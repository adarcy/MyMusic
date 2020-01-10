package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Song;

/**
 * 音乐，更多
 * Created by smile on 2018/5/31.
 */

public class SongMoreDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    //private PlayListManager playListManager;
    private OnMoreListener onMoreListener;

    private Song song;
    private TextView tv_song;
    private TextView tv_comment_count;
    private TextView tv_album;
    private TextView tv_artist;
    private LinearLayout ll_next_play;
    private LinearLayout ll_collection;
    private LinearLayout ll_download;
    private LinearLayout ll_comment;
    private LinearLayout ll_delete;
    private boolean isShowDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_song_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_song = view.findViewById(R.id.tv_song);
        ll_next_play = view.findViewById(R.id.ll_next_play);
        ll_collection = view.findViewById(R.id.ll_collection);
        ll_download = view.findViewById(R.id.ll_download);
        ll_comment = view.findViewById(R.id.ll_comment);
        tv_comment_count = view.findViewById(R.id.tv_comment_count);
        tv_album = view.findViewById(R.id.tv_album);
        tv_artist = view.findViewById(R.id.tv_artist);
        ll_delete = view.findViewById(R.id.ll_delete);

        if (isShowDelete) {
            ll_delete.setVisibility(View.VISIBLE);
        }

        initData();
        initListener();

    }

    private void initData() {
        //playListManager = MusicPlayerService.getPlayListManager(getContext());

        tv_song.setText(getContext().getResources().getString(R.string.song_detail, song.getTitle()));
        tv_comment_count.setText(getContext().getResources().getString(R.string.comment_count, song.getComments_count()));
        tv_artist.setText(getContext().getResources().getString(R.string.artist, song.getArtist_name()));
        tv_album.setText(getContext().getResources().getString(R.string.album, song.getAlbum_title()));
    }

    private void initListener() {
        ll_next_play.setOnClickListener(this);
        ll_collection.setOnClickListener(this);
        ll_download.setOnClickListener(this);
        ll_delete.setOnClickListener(this);
    }

    public static void show(FragmentManager fragmentManager, Song song, OnMoreListener onMoreListener) {
        SongMoreDialogFragment.show(fragmentManager,song,false,onMoreListener);
    }

    public static void show(FragmentManager fragmentManager, Song song, boolean isShowDelete, OnMoreListener onMoreListener) {
        SongMoreDialogFragment songMoreDialogFragment = new SongMoreDialogFragment();
        songMoreDialogFragment.setSong(song);
        songMoreDialogFragment.setShowDelete(isShowDelete);
        songMoreDialogFragment.setListener(onMoreListener);
        songMoreDialogFragment.show(fragmentManager, "SortDialogFragment");
    }

    private void setShowDelete(boolean isShowDelete) {
        this.isShowDelete=isShowDelete;
    }

    private void setListener(OnMoreListener onMoreListener) {
        this.onMoreListener = onMoreListener;
    }

    private void setSong(Song song) {
        this.song = song;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_next_play:
                this.dismiss();
                //playListManager.nextPlay(song);
                break;

            case R.id.ll_collection:
                this.dismiss();
                if (onMoreListener != null) {
                    onMoreListener.onCollectionClick(song);
                }
                break;

            case R.id.ll_download:
                this.dismiss();
                if (onMoreListener != null) {
                    onMoreListener.onDownloadClick(song);
                }
                break;

            case R.id.ll_delete:
                this.dismiss();
                //playListManager.delete(song);
                if (onMoreListener != null) {
                    onMoreListener.onDeleteClick(song);
                }
                break;
        }
    }

    public interface OnMoreListener {
        void onCollectionClick(Song song);

        void onDownloadClick(Song song);

        void onDeleteClick(Song song);
    }
}
