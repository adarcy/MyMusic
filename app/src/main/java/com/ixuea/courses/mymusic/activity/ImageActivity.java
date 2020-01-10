package com.ixuea.courses.mymusic.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ToastUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class ImageActivity extends BaseCommonActivity implements View.OnClickListener {

    private static final String TAG = "TAG";
    private PhotoView pv;
    private Button bt_save;
    private String uri;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //开启窗口过度动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //不要导入Support包中的
        ChangeBounds transition = new ChangeBounds();

        //设置动画
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementExitTransition(transition);
        getWindow().setSharedElementReenterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);

        setContentView(R.layout.activity_image);
    }

    @Override
    protected void initViews() {
        super.initViews();
        pv = findViewById(R.id.pv);
        bt_save = findViewById(R.id.bt_save);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        uri = getIntent().getStringExtra(Consts.STRING);
        id = getIntent().getStringExtra(Consts.ID);
        ImageUtil.show(getActivity(), pv, uri);
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //这里的后缀名可以从uri上截取
        final File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), id+".jpg");
        if (destFile.exists()) {
            ToastUtil.showSortToast(getApplicationContext(), "已经存在了!");
            return;
        }

        //glide4的下载明显比glide3复杂
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Context context = getApplicationContext();
                    //FutureTarget会阻塞，所以需要在子线程调用
                    FutureTarget<File> target = Glide.with(context)
                            .asFile()
                            .load(ImageUtil.getImageURI(uri))
                            .submit();
                    //将文件拷贝到我们的目陆中
                    final File imageFile = target.get();
                    FileUtils.copyFile(imageFile,destFile);
                    Log.d(TAG, "download album: "+imageFile.getAbsolutePath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showSortToast(getApplicationContext(), "封面下载完成!");
                            sendMediaChanged(destFile);
                        }
                    });
                } catch (Exception e) {
                    //e.printStackTrace();
                    ToastUtil.showSortToast(getApplicationContext(), "下载失败,请稍后再试!");
                }
            }
        }).start();
    }

    private void sendMediaChanged(File destFile) {
        //try {
        //不用在插入图片了，因为我们已经保存到了图库中了
        //MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
        //        destFile.getAbsolutePath(), destFile.getName(), null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(destFile);
        intent.setData(uri);
        sendBroadcast(intent);
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}

    }
}
