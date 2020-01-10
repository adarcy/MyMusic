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
 * 评论，更多
 * Created by smile on 2018/5/31.
 */

public class CommentMoreDialogFragment extends DialogFragment {
    private DialogInterface.OnClickListener onClickListener;
    private int selectIndex;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.comment_more, onClickListener);
        return builder.create();
    }

    public void show(FragmentManager fragmentManager, DialogInterface.OnClickListener onClickListener) {
        this.selectIndex = selectIndex;
        this.onClickListener = onClickListener;
        show(fragmentManager, "CommentMoreDialogFragment");
    }
}

