package com.ixuea.courses.mymusic.api;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.domain.Advertisement;
import com.ixuea.courses.mymusic.domain.Comment;
import com.ixuea.courses.mymusic.domain.Feed;
import com.ixuea.courses.mymusic.domain.List;
import com.ixuea.courses.mymusic.domain.SearchHot;
import com.ixuea.courses.mymusic.domain.Session;
import com.ixuea.courses.mymusic.domain.Song;
import com.ixuea.courses.mymusic.domain.Topic;
import com.ixuea.courses.mymusic.domain.User;
import com.ixuea.courses.mymusic.domain.Video;
import com.ixuea.courses.mymusic.domain.param.FeedParam;
import com.ixuea.courses.mymusic.domain.response.DetailResponse;
import com.ixuea.courses.mymusic.domain.response.ListResponse;
import com.ixuea.courses.mymusic.interceptor.HttpLoggingInterceptor;
import com.ixuea.courses.mymusic.util.Consts;
import com.ixuea.courses.mymusic.util.LogUtil;
import com.ixuea.courses.mymusic.util.SharedPreferencesUtil;
import com.readystatesoftware.chuck.ChuckInterceptor;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by smile on 02/03/2018.
 */

public class Api {
    private static final String SONG_ID = "song_id";
    private static Api instance;
    private static Service service;

    Api() {
        // 创建 OKHttpClient
        //File file = new File(AppContext.getContext().getCacheDir(), "http");
        //创建缓存
        //Cache cache = new Cache(file, 1024 * 1024 * 1000);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //builder.cache(cache);
        builder.connectTimeout(Consts.TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(Consts.TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(Consts.TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        if (LogUtil.isDebug) {
            builder.addInterceptor(new HttpLoggingInterceptor());
            builder.addNetworkInterceptor(new StethoInterceptor());
            builder.addInterceptor(new ChuckInterceptor(AppContext.getContext()));
        }

        //用对网络请求缓存，详细的查看《详解OKHttp》课程
        //builder.addInterceptor(FORCE_CACHE_NETWORK_DATA_INTERCEPTOR);
        //builder.addNetworkInterceptor(FORCE_CACHE_NETWORK_DATA_INTERCEPTOR);

        //公共请求参数
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                SharedPreferencesUtil sp = SharedPreferencesUtil.getCurrentInstance();
                Request request = chain.request();
                if (sp.isLogin()) {
                    String userId = sp.getUserId();
                    String token = sp.getToken();

                    if (LogUtil.isDebug) {
                        LogUtil.d("token:" + token + "," + userId);
                    }

                    request = chain.request().newBuilder()
                            .addHeader("User", userId)
                            .addHeader("Authorization", token)
                            .build();
                }
                return chain.proceed(request);
            }
        });

        // 添加公共参数拦截器


        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Consts.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(Service.class);
    }

    public static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public Observable<DetailResponse<Session>> login(User user) {
        return service.login(user);
    }

    public Observable<DetailResponse<Session>> register(User user) {
        return service.register(user);
    }

    public Observable<DetailResponse<Session>> logout(String id) {
        return service.logout(id);
    }

    public Observable<DetailResponse<User>> userDetail(String id) {
        return service.userDetail(id);
    }

    public Observable<DetailResponse<User>> userDetailByNickname(String nickname) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Consts.NICKNAME, nickname);
        return service.userDetailByNickname(data);
    }

    public Observable<ListResponse<List>> lists() {
        HashMap<String, String> query = new HashMap<>();
        return service.lists(query);
    }

    public Observable<ListResponse<SearchHot>> prompt(String content) {
        HashMap<String, String> query = new HashMap<>();
        query.put(Consts.TITLE,content);
        return service.prompt(query);
    }

    public Observable<ListResponse<SearchHot>> searchHot() {
        HashMap<String, String> query = new HashMap<>();
        return service.searchHot(query);
    }

    public Observable<ListResponse<Song>> searchSong(String title) {
        HashMap<String, String> query = new HashMap<>();
        query.put(Consts.TITLE,title);
        return service.searchSong(query);
    }

    public Observable<DetailResponse<List>> createList(List list) {
        return service.createList(list);
    }

    public Observable<DetailResponse<Comment>> createComment(Comment comment) {
        return service.createComment(comment);
    }

    public Observable<DetailResponse<List>> collectionList(String listId) {
        return service.collectionList(listId);
    }

    public Observable<DetailResponse<Comment>> like(String commentId) {
        return service.like(commentId);
    }

    public Observable<DetailResponse<User>> follow(String userId) {
        return service.follow(userId);
    }

    public Observable<DetailResponse<User>> unFollow(String userId) {
        return service.unFollow(userId);
    }

    public Observable<DetailResponse<List>> cancelCollectionList(String id) {
        return service.cancelCollectionList(id);
    }

    public Observable<DetailResponse<Comment>> unlike(String id) {
        return service.unlike(id);
    }

    public Observable<DetailResponse<List>> addSongInSheet(String songId,String listId) {
        return service.addSongInSheet(songId,listId);
    }

    public Observable<DetailResponse<List>> deleteSongInSheet(String songId,String sheetId) {
        return service.deleteSongInSheet(songId,sheetId);
    }

    public Observable<ListResponse<List>> listsMyCreate() {
        return service.listsMyCreate();
    }

    public Observable<ListResponse<List>> listsMyCollection() {
        return service.listsMyCollection();
    }

    public Observable<ListResponse<Song>> songs() {
        return service.songs();
    }

    public Observable<DetailResponse<Song>> songsDetail(String id) {
        return service.songsDetail(id);
    }

    public Observable<DetailResponse<List>> listDetail(String id) {
        return service.listDetail(id);
    }

    //public Observable<ListResponse<Feed>> feedsByTopic(String topic) {
    //    HashMap<String, String> data = new HashMap<>();
    //    data.put(Consts.TOPIC,topic);
    //    return service.feeds(data);
    //}
    //
    ///**
    // * 获取动态，传UserId数据就是该用户的，不传就是全部
    // * @param userId
    // * @return
    // */
    public Observable<ListResponse<Feed>> feeds(String userId, int pageSize) {
        HashMap<String, String> data = new HashMap<>();
        if (StringUtils.isNotBlank(userId)) {
            data.put(Consts.USER_ID,userId);
        }
        data.put(Consts.PAGE,String.valueOf(pageSize));
        return service.feeds(data);
    }

    public Observable<DetailResponse<Feed>> createFeed(FeedParam data) {
        return service.createFeed(data);
    }

    public Observable<ListResponse<Comment>> comments(Map<String,String> data) {
        return service.comments(data);
    }

    public Observable<ListResponse<User>> myFriends(String id,String nickname) {
        HashMap<String, String> data = new HashMap<>();

        //根据nickname查找
        if (StringUtils.isNotEmpty(nickname)) {
            data.put(Consts.FILTER, nickname);
        }
        return service.following(id,data);
    }

    public Observable<ListResponse<User>> myFans(String id,String nickname) {
        HashMap<String, String> data = new HashMap<>();

        //根据nickname查找
        if (StringUtils.isNotEmpty(nickname)) {
            data.put(Consts.FILTER, nickname);
        }
        return service.followers(id,data);
    }

    public Observable<ListResponse<Topic>> topics(String title) {
        HashMap<String, String> data = new HashMap<>();
        if (StringUtils.isNotEmpty(title)) {
            data.put(Consts.FILTER, title);
        }
        return service.topics(data);
    }

    public Observable<DetailResponse<Topic>> topicDetail(String id) {
        HashMap<String, String> data = new HashMap<>();
        return service.topicDetail(id,data);
    }

    public Observable<DetailResponse<Topic>> topicDetailByTitle(String title) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Consts.TITLE,title);
        return service.topicDetail(String.valueOf(-1),data);
    }

    public Observable<ListResponse<Video>> videos() {
        HashMap<String, String> data = new HashMap<>();
        return service.videos(data);
    }

    public Observable<DetailResponse<Video>> videoDetail(String id) {
        return service.videoDetail(id);
    }

    public Observable<ListResponse<Advertisement>> advertisements() {
        return service.advertisements();
    }

    //public Observable<DetailResponse<Lyric>>  lyricDetailWithBySongId(String id) {
    //    HashMap<String, String> data = new HashMap<>();
    //    data.put(SONG_ID,id);
    //    return service.lyricDetailWithBySongId(data);
    //}
}
