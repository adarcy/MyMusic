package com.ixuea.courses.mymusic.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.UserDetailAdapter;
import com.ixuea.courses.mymusic.api.Api;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.reactivex.HttpListener;
import com.ixuea.courses.mymusic.util.Consts;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserDetailActivity extends BaseTitleActivity {

    @BindView(R.id.tabs)
    MagicIndicator tabs;

    @BindView(R.id.vp)
    ViewPager vp;

    @BindView(R.id.abl)
    AppBarLayout abl;

    @BindView(R.id.iv_avatar)
    ImageView iv_avatar;

    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    @BindView(R.id.tv_info)
    TextView tv_info;

    @BindView(R.id.bt_follow)
    Button bt_follow;

    @BindView(R.id.bt_send_message)
    Button bt_send_message;
    private String nickname;
    private String id;
    private User user;
    private UserDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        vp.setOffscreenPageLimit(3);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        nickname = getIntent().getStringExtra(Consts.NICKNAME);
        id = getIntent().getStringExtra(Consts.ID);

        if (StringUtils.isNotEmpty(id)) {
            //如果Id，不为空，就通过Id查询
            fetchDataById(id);
        } else if (StringUtils.isNotEmpty(nickname)) {
            //通过昵称查询，主要是用在@昵称中
            fetchDataByNickname(nickname);
        } else {
            finish();
        }
    }

    private void fetchDataByNickname(String nickname) {
        Api.getInstance().userDetailByNickname(nickname).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<User>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    private void fetchDataById(String id) {
        Api.getInstance().userDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<User>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    public void next(User user) {
        this.user=user;
        setupUI(user.getId());

        //ImageUtil.showCircle(getActivity(), iv_avatar, user.getAvatar());
        RequestOptions options = new RequestOptions();
        options.circleCrop();
        RequestBuilder<Bitmap> bitmapRequestBuilder = Glide.with(this).asBitmap().apply(options).load(user.getAvatar());
        bitmapRequestBuilder.into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                iv_avatar.setImageBitmap(resource);

                Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (swatch != null) {
                            int rgb = swatch.getRgb();
                            //toolbar.setBackgroundColor(rgb);
                            abl.setBackgroundColor(rgb);
                            //设置状态栏
                            if (android.os.Build.VERSION.SDK_INT >= 21) {
                                Window window = getWindow();
                                window.setStatusBarColor(rgb);
                                window.setNavigationBarColor(rgb);
                            }
                        }
                    }
                });
            }
        });

        tv_nickname.setText(user.getNickname());
        tv_info.setText(getResources().getString(R.string.user_detail_count_info,user.getFollowings_count(),user.getFollowers_count()));

        showFollowStatus();
    }

    private void showFollowStatus() {
        if (user.getId().equals(sp.getUserId())) {
            //自己，隐藏关注按钮，隐藏发送消息按钮
            bt_follow.setVisibility(View.GONE);
            bt_send_message.setVisibility(View.GONE);
        } else {
            //判断我是否关注该用户
            bt_follow.setVisibility(View.VISIBLE);
            if (user.isFollowing()) {
                //已经关注
                bt_follow.setText("取消关注");
                bt_send_message.setVisibility(View.VISIBLE);
            } else {
                //没有关注
                bt_follow.setText("关注");
                bt_send_message.setVisibility(View.GONE);
            }
        }
    }

    private void setupUI(String id) {
        adapter = new UserDetailAdapter(getActivity(), getSupportFragmentManager());
        adapter.setUserId(id);
        vp.setAdapter(adapter);

        final ArrayList<Integer> datas = new ArrayList<>();
        datas.add(0);
        datas.add(1);
        datas.add(2);
        adapter.setDatas(datas);

        //将TabLayout和ViewPager关联起来
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return datas.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color.text_white));
                colorTransitionPagerTitleView.setSelectedColor(Color.WHITE);
                colorTransitionPagerTitleView.setText(adapter.getPageTitle(index));
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vp.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(Color.WHITE);
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(true);
        tabs.setNavigator(commonNavigator);

        ViewPagerHelper.bind(tabs, vp);
    }

    @OnClick(R.id.bt_follow)
    public void bt_follow() {
        if (user.isFollowing()) {
            //取消关注
            Api.getInstance().unFollow(user.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpListener<DetailResponse<User>>(getActivity()) {
                        @Override
                        public void onSucceeded(DetailResponse<User> data) {
                            super.onSucceeded(data);
                            user.setFollowing(0);

                            //也可以调用服务端，以服务端的数据刷新界面
                            showFollowStatus();
                        }
                    });
        } else {
            //关注
            Api.getInstance().follow(user.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpListener<DetailResponse<User>>(getActivity()) {
                        @Override
                        public void onSucceeded(DetailResponse<User> data) {
                            super.onSucceeded(data);
                            user.setFollowing(1);
                            showFollowStatus();
                        }
                    });
        }
    }

    @OnClick(R.id.bt_send_message)
    public void bt_send_message() {
        ConversationActivity.start(this,user.getId());
    }
}
