package com.ixuea.courses.mymusic.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.ixuea.courses.mymusic.R;

/**
 * 歌曲排序
 * Created by smile on 2018/5/31.
 */

public class SortDialogFragment extends DialogFragment {
    private static final String[] items={"默认","单曲名","专辑名"};
    private DialogInterface.OnClickListener onClickListener;
    private int selectIndex;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_song_sort).setSingleChoiceItems(items, selectIndex, onClickListener);
        return builder.create();
    }

    public void show(FragmentManager fragmentManager, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        this.selectIndex = selectIndex;
        this.onClickListener = onClickListener;
        show(fragmentManager,"SortDialogFragment");
    }
}
