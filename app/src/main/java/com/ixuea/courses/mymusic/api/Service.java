package com.ixuea.courses.mymusic.api;


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

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by smile on 02/03/2018.
 */

public interface Service {
    /**
     * 登陆
     * @param user
     * @return
     */
    @POST("sessions.json")
    Observable<DetailResponse<Session>> login(@Body User user);


    /**
     * 退出
     * @param id
     * @return
     */
    @DELETE("sessions/{id}.json")
    Observable<DetailResponse<Session>> logout(@Path("id") String id);

    /**
     * 注册，注册完成后返回是登陆的信息
     * @param user
     * @return
     */
    @POST("users.json")
    Observable<DetailResponse<Session>> register(@Body User user);


    /**
     * 获取用户详情
     * @param id
     * @return
     */
    @GET("users/{id}.json")
    Observable<DetailResponse<User>> userDetail(@Path("id") String id);

    /**
     * 获取话题详情
     * @param id
     * @return
     */
    @GET("topics/{id}.json")
    Observable<DetailResponse<Topic>> topicDetail(@Path("id") String id, @QueryMap Map<String, String> data);

    /**
     * 根据nickname,获取用户详情
     * @param data
     * @return
     */
    @GET("users/-1.json")
    Observable<DetailResponse<User>> userDetailByNickname(@QueryMap Map<String, String> data);



    /**
     * 歌单列表
     * @return
     */
    @GET("sheets.json")
    Observable<ListResponse<List>> lists(@QueryMap Map<String, String> data);

    /**
     * 我创建的歌单列表
     * @return
     */
    @GET("sheets/create.json")
    Observable<ListResponse<List>> listsMyCreate();

    /**
     * 我收藏的歌单列表
     * @return
     */
    @GET("sheets/collect.json")
    Observable<ListResponse<List>> listsMyCollection();

    /**
     * 搜索提示列表
     * @return
     */
    @GET("searches/prompts.json")
    Observable<ListResponse<SearchHot>> prompt(@QueryMap Map<String, String> data);

    /**
     * 热门搜索列表
     * @return
     */
    @GET("searches/hots.json")
    Observable<ListResponse<SearchHot>> searchHot(@QueryMap Map<String, String> data);

    /**
     * 搜索歌曲
     * @return
     */
    @GET("searches/songs.json")
    Observable<ListResponse<Song>> searchSong(@QueryMap Map<String, String> data);

    /**
     * 创建歌单
     * @return
     */
    @POST("sheets.json")
    Observable<DetailResponse<List>> createList(@Body List data);

    /**
     * 创建评论
     * @return
     */
    @POST("comments.json")
    Observable<DetailResponse<Comment>> createComment(@Body Comment data);

    /**
     * 收藏歌单
     * @return
     */
    @FormUrlEncoded
    @POST("collections.json")
    Observable<DetailResponse<List>> collectionList(@Field("sheet_id") String list_id);

    /**
     * 评论点赞
     * @return
     */
    @FormUrlEncoded
    @POST("likes.json")
    Observable<DetailResponse<Comment>> like(@Field("comment_id") String comment_id);

    /**
     * 关注用户
     * @return
     */
    @FormUrlEncoded
    @POST("relationships.json")
    Observable<DetailResponse<User>> follow(@Field("id") String user_id);

    /**
     * 取消关注用户
     * @return
     */
    @DELETE("relationships/{user_id}.json")
    Observable<DetailResponse<User>> unFollow(@Path("user_id") String user_id);

    /**
     * 收藏歌曲到歌单
     * @return
     */
    @FormUrlEncoded
    @POST("contacts.json")
    Observable<DetailResponse<List>> addSongInSheet(@Field("song_id") String songId, @Field("sheet_id") String sheet_id);

    /**
     * 将歌曲从歌单中删除
     * @return
     */
    @DELETE("contacts/{song_id}.json")
    Observable<DetailResponse<List>> deleteSongInSheet(@Path("song_id") String songId, @Query("sheet_id") String sheet_id);


    /**
     * 取消收藏歌单
     * @return
     */
    @DELETE("collections/{id}.json")
    Observable<DetailResponse<List>> cancelCollectionList(@Path("id") String id);

    /**
     * 取消评论点赞
     * @return
     */
    @DELETE("likes/{id}.json")
    Observable<DetailResponse<Comment>> unlike(@Path("id") String id);


    /**
     * 单曲列表
     * @return
     */
    @GET("songs.json")
    Observable<ListResponse<Song>> songs();

    /**
     * 获取歌曲详情
     * @param id
     * @return
     */
    @GET("songs/{id}.json")
    Observable<DetailResponse<Song>> songsDetail(@Path("id") String id);

    /**
     * 获取歌单详情
     * @param id
     * @return
     */
    @GET("sheets/{id}.json")
    Observable<DetailResponse<List>> listDetail(@Path("id") String id);

    /**
     * 评论列表
     * @param data
     * @return
     */
    @GET("comments.json")
    Observable<ListResponse<Comment>> comments(@QueryMap Map<String, String> data);

    /**
     * 动态列表
     * @param data
     * @return
     */
    @GET("feeds.json")
    Observable<ListResponse<Feed>> feeds(@QueryMap Map<String, String> data);

    /**
     * 发布动态
     * @param feed
     * @return
     */
    @POST("feeds.json")
    Observable<DetailResponse<Feed>> createFeed(@Body FeedParam feed);

  //  /**
  //   * 用户列表
  //   * @param data
  //   * @return
  //   */
  //  @GET("users.json")
  //  Observable<ListResponse<User>> users(@QueryMap Map<String, String> data);

    /**
     * 我关注的人
     * @param id
     * @return
     */
    @GET("users/{id}/following.json")
    Observable<ListResponse<User>> following(@Path("id") String id, @QueryMap Map<String, String> data);

    /**
     * 关注我关注的人
     * @param id
     * @return
     */
    @GET("users/{id}/followers.json")
    Observable<ListResponse<User>> followers(@Path("id") String id, @QueryMap Map<String, String> data);

    /**
     * 话题列表
     * @param data
     * @return
     */
    @GET("topics.json")
    Observable<ListResponse<Topic>> topics(@QueryMap Map<String, String> data);

  /**
     * 视频列表
     * @param data
     * @return
     */
    @GET("videos.json")
    Observable<ListResponse<Video>> videos(@QueryMap Map<String, String> data);

    /**
     * 视频详情
     * @param id
     * @return
     */
    @GET("videos/{id}.json")
    Observable<DetailResponse<Video>> videoDetail(@Path("id") String id);

  //  /**
  //   * 查找歌词
  //   * @return
  //   */
  //  @GET("lyrics.json")
  //  Observable<DetailResponse<Lyric>> lyricDetailWithBySongId(@QueryMap Map<String, String> data);

    /**
     * 广告列表
     * @return
     */
    @GET("advertisements.json")
    Observable<ListResponse<Advertisement>> advertisements();
}
