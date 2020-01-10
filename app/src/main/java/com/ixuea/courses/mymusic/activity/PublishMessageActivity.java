package com.ixuea.courses.mymusic.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.adapter.ImageSelectAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.Feed;
import com.ixuea.courses.mymusic.domain.event.PublishMessageEvent;
import com.ixuea.courses.mymusic.domain.param.FeedParam;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.OSSUtil;
import com.ixuea.courses.mymusic.util.ToastUtil;
import com.ixuea.courses.mymusic.util.UUIDUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PublishMessageActivity extends BaseTitleActivity {

    private static final int REQUEST_SELECT_IMAGE = 10;
    private EditText et_message;
    private RecyclerView rv;
    private ImageSelectAdapter imageSelectAdapter;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_message);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        et_message=findViewById(R.id.et_message);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        rv.setLayoutManager(layoutManager);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        imageSelectAdapter = new ImageSelectAdapter(getActivity(), R.layout.item_select_iamge);
        imageSelectAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                Object data = imageSelectAdapter.getData(position);
                if (data instanceof LocalMedia) {
                    //预览界面
                } else {
                    //选择图片图片
                    selectImage();
                }
            }
        });
        rv.setAdapter(imageSelectAdapter);

        setData(new ArrayList<Object>());
    }

    private void setData(ArrayList<Object> objects) {
        if (objects.size() != 9) {
            //选了9张图片，就不显示添加按钮
            objects.add(R.drawable.ic_add_grey);
        }

        imageSelectAdapter.setData(objects);
    }

    private void selectImage() {
        ArrayList<LocalMedia> selectedImage = getSelectedImages();

        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                //.theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                //.previewVideo()// 是否可预览视频 true or false
                //.enablePreviewAudio() // 是否可播放音频 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                //.enableCrop()// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //.hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
                //.isGif()// 是否显示gif图片 true or false
                //.compressSavePath(getPath())//压缩图片保存地址
                //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                //.circleDimmedLayer()// 是否圆形裁剪 true or false
                //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                //.openClickSound()// 是否开启点击声音 true or false
                .selectionMedia(selectedImage)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                //.minimumCompressSize(100)// 小于100kb的图片不压缩
                //.synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                //.videoQuality()// 视频陆制质量 0 or 1 int
                //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                //.recordVideoSecond()//视频秒数陆制 默认60s int
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code


    }

    @NonNull
    private ArrayList<LocalMedia> getSelectedImages() {
        List<Object> datas = imageSelectAdapter.getDatas();
        ArrayList<LocalMedia> selectedImage = new ArrayList<>();
        for (Object o : datas) {
            if (o instanceof LocalMedia) {
                selectedImage.add((LocalMedia) o);
            }
        }
        return selectedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    ArrayList<Object> objects = new ArrayList<>();
                    objects.addAll(selectList);
                    setData(objects);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send) {
            sendMessage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        message = et_message.getText().toString().trim();
        if (StringUtils.isBlank(message)) {
            ToastUtil.showSortToast(getActivity(),R.string.hint_message);
            return;
        }

        if (message.length() > 140) {
            ToastUtil.showSortToast(getActivity(),R.string.content_length_error);
            return;
        }

        ArrayList<LocalMedia> selectedImages = getSelectedImages();
        if (selectedImages.size() > 0) {
            //有图片，先上传图片
            uploadImage(selectedImages);
        } else {
            saveMessage(null);
        }
    }

    private void uploadImage(ArrayList<LocalMedia> selectedImages) {
        final OSSClient oss = OSSUtil.getInstance(getActivity());
        new AsyncTask<List<LocalMedia>, Integer, List<String>>() {
            @Override
            protected List<String> doInBackground(List<LocalMedia>... params) {
                try {
                    ArrayList<String> results = new ArrayList<>();
                    for (Object o : params[0]) {
                        if (o instanceof LocalMedia) {
                            //上传
                            //OSS如果没有特殊需求建议不要分目陆，如果一定要分不要让目陆名前面连续
                            //例如时间戳倒过来，如果连续请求达到一定量级会有性能影响
                            //https://help.aliyun.com/document_detail/64945.html
                            String destFileName= UUIDUtil.getUUID()+".jpg";
                            PutObjectRequest put = new PutObjectRequest(Consts.OSS_BUCKET_NAME, destFileName, ((LocalMedia) o).getCompressPath());
                            PutObjectResult putResult = oss.putObject(put);

                            results.add(destFileName);
                        }
                    }

                    return results;
                } catch (Exception e) {
                    // 服务异常
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<String> data) {
                super.onPostExecute(data);
                if (data!=null&&data.size()>0) {
                    saveMessage(data);
                } else {
                    ToastUtil.showSortToast(getActivity(), getString(R.string.upload_image_error));
                }
            }
        }.execute(selectedImages);
    }

    private void saveMessage(List<String> data) {
        FeedParam feed = new FeedParam();
        feed.setContent(message);
        feed.setImages(data);
        Api.getInstance().createFeed(feed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<Feed>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<Feed> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    public void next(Feed feed) {
        EventBus.getDefault().post(new PublishMessageEvent());
        finish();
    }
}
