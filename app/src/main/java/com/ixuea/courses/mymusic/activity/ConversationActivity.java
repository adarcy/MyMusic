package com.ixuea.courses.mymusic.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.ConversationDetailAdapter;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.event.OnMessageEvent;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.manager.impl.UserManagerImpl;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.ToastUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

public class ConversationActivity extends BaseTitleActivity implements TextWatcher, View.OnClickListener {

    private static final String TAG = "ConversationActivity";

    private ImageView iv_send;
    private ImageView ic_select_image;
    private EditText et_content;
    private RecyclerView rv;

    private UserManager userManager;
    private ConversationDetailAdapter adapter;

    private RongIMClient imClient;
    private int oldMessageId = -1;
    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        iv_send = findViewById(R.id.iv_send);
        ic_select_image = findViewById(R.id.ic_select_image);
        et_content = findViewById(R.id.et_content);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);
    }


    @Override
    protected void initDatas() {
        super.initDatas();
        EventBus.getDefault().register(this);

        targetId = getIntent().getStringExtra(Consts.ID);

        imClient = RongIMClient.getInstance();
        userManager = UserManagerImpl.getInstance(getApplicationContext());

        //由于我们这里没有区分消失具体的状态
        //比如：已读还是未读，所以只要进入了会话界面，该会话下面的所有消息都表示已读
        imClient.clearMessagesUnreadStatus(Conversation.ConversationType.PRIVATE, targetId, null);

        adapter = new ConversationDetailAdapter(getActivity());
        rv.setAdapter(adapter);

        fetchData();
    }

    private void fetchData() {
        userManager.getUser(targetId, new UserManager.OnUserListener() {
            @Override
            public void onUser(User user) {
                setTitle(user.getNickname());
            }
        });

        loadMore();
    }

    private void loadMore() {
        imClient.getHistoryMessages(Conversation.ConversationType.PRIVATE, targetId, oldMessageId, Consts.DEFAULT_MESSAGE_COUNT, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                    //默认的排序是时间由近到远，因为聊天界面是倒着显示，所以排序也要倒过来
                    Collections.sort(messages, new Comparator<Message>() {
                        @Override
                        public int compare(Message o1, Message o2) {
                            if (o1.getSentTime() > o2.getSentTime()) {
                                return 1;
                            } else if (o1.getSentTime() < o2.getSentTime()) {
                                return -1;
                            }
                            return 0;
                        }
                    });

                    adapter.addData(0, messages);

                    if (oldMessageId == -1) {
                        //如果有下拉操作，就不滚动，当前用可以根据具体的业务逻辑来
                        rv.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollToBottom();
                            }
                        });
                    }

                    //排序后第一条就是最久的一条，加载更多也就是用这个id
                    oldMessageId = messages.get(0).getMessageId();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        et_content.addTextChangedListener(this);
        iv_send.setOnClickListener(this);
        ic_select_image.setOnClickListener(this);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_IDLE == newState && !rv.canScrollVertically(-1)) {
                    //canScrollVertically(1)是否能向上滚动，false表示已经滚动到底部
                    //canScrollVertically(-1)是否能向下滚动，false表示已经滚动到顶部
                    //已经到顶部
                    loadMore();
                }
            }
        });
    }


    private void scrollToBottom() {
        rv.scrollToPosition(adapter.getItemCount() - 1);

    }


    /**
     *
     * @param context
     * @param id
     */
    public static void start(Context context, String id) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(Consts.ID, id);
        context.startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                sendTextMessage();
                break;
            case R.id.ic_select_image:
                selectImage();
                break;
        }
    }

    private void sendTextMessage() {
        String content = et_content.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtil.showSortToast(getActivity(), getString(R.string.enter_content));
            return;
        }

        TextMessage textMessage = TextMessage.obtain(content);
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, targetId, textMessage, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                // 消息成功存到本地数据库的回调
                Log.d(TAG, "onAttached: " + message);

            }

            @Override
            public void onSuccess(Message message) {
                // 消息发送成功的回调
                Log.d(TAG, "onSuccess: " + message);
                clearEditText();
                addMessage(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                // 消息发送失败的回调
                Log.d(TAG, "onError: " + message + "," + errorCode);
            }
        });
    }

    private void clearEditText() {
        et_content.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnMessageEvent event) {
        Message message = event.getMessage();
        if (!message.getSenderUserId().equals(targetId)) {
            return;
        }

        //既然在当前界面，那收到的消息也是已读的
        // 更新内存中消息的已读状态
        message.getReceivedStatus().setRead();
        // 更新数据库中消息的状态
        RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), message.getReceivedStatus(), null);

        addMessage(message);
    }

    private void addMessage(Message message) {
        adapter.addData(message);
        scrollToBottom();
    }

    private void selectImage() {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                //.theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                //.maxSelectNum(9)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
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
                //.selectionMedia(selectedImage)// 是否传入已选图片 List<LocalMedia> list
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList!=null&&selectList.size()>0){
                        sendImageMessage(selectList.get(0));
                    }
                    break;
            }
        }
    }

    private void sendImageMessage(LocalMedia localMedia) {
        String compressPath = localMedia.getCompressPath();
        Uri uri = Uri.fromFile(new File(compressPath));
        ImageMessage message = ImageMessage.obtain(uri,uri);
        RongIMClient.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE, targetId, message, null, null, new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {
                Log.d(TAG, "image onAttached: " + message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                Log.d(TAG, "image onError: " + message+","+errorCode);
            }

            @Override
            public void onSuccess(Message message) {
                addMessage(message);
                Log.d(TAG, "image onSuccess: " + message);
            }

            @Override
            public void onProgress(Message message, int i) {
                Log.d(TAG, "onProgress: "+message.toString()+","+i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
