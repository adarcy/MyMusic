package com.ixuea.courses.mymusic.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.ToastUtil;

import org.apache.commons.lang3.StringUtils;


/**
 * 创建新歌单
 * Created by smile on 2018/5/31.
 */

public class CreateListDialogFragment extends DialogFragment {

    private OnConfirmCreateListListener onConfirmCreateListListener;
    private EditText et;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        et = new EditText(getActivity());
        et.setHint(R.string.enter_list_name);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getActivity());
        inputDialog.setTitle(R.string.new_list).setView(et);
        inputDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = et.getText().toString();
                if (StringUtils.isNotBlank(text)) {
                    dialog.dismiss();
                    onConfirmCreateListListener.onConfirmCreateListClick(text);
                } else {
                    ToastUtil.showSortToast(getActivity(),R.string.enter_list_name);
                }

            }
        });
        return inputDialog.create();
    }

    private void initData() {
    }

    private void initListener() {
    }

    public static void show(FragmentManager fragmentManager, OnConfirmCreateListListener onConfirmCreateListListener) {
        CreateListDialogFragment songMoreDialogFragment = new CreateListDialogFragment();
        songMoreDialogFragment.setListener(onConfirmCreateListListener);
        songMoreDialogFragment.show(fragmentManager, "CreateListDialogFragment");
    }

    private void setListener(OnConfirmCreateListListener onConfirmCreateListListener) {
        this.onConfirmCreateListListener = onConfirmCreateListListener;
    }

    public interface OnConfirmCreateListListener{
        void onConfirmCreateListClick(String text);
    }

}
