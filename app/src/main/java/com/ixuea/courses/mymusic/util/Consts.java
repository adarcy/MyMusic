package com.ixuea.courses.mymusic.util;

import com.ixuea.courses.mymusic.BuildConfig;

/**
 * Created by smile on 2018/6/21.
 */

public class Consts {
    public static final String RESOURCE_PREFIX = BuildConfig.RESOURCE_PREFIX;
    public static final String ENDPOINT = BuildConfig.ENDPOINT;

    public static final long TIME_OUT = 5;
    public static final String ID = "ID";
    public static final String TYPE = "type";
    public static final int TYPE_MUSIC = 0;
    public static final String ID2 = "ID2";
    public static final String STRING = "STRING";
    public static final String DATA = "DATA";

    public static final String TRUE = "TRUE";
    public static final String TRUE1 = "TRUE1";
    public static final java.lang.String MODEL = "model";

    public static final String P = "P";
    public static final String PAGE = "page";
    public static final String USER_ID = "user_id";
    public static final String INT = "INT";
    public static final String BANNER = "BANNER";
    public static final String TITLE = "title";
    public static final String DETAIL = "DETAIL";
    public static final String PRICE = "PRICE";
    public static final String SECTION_ID = "section_id";
    public static final String COURSE_ID = "course_id";
    public static final String BOOK_ID = "book_id";
    public static final java.lang.String ORDERS_COUNT = "ORDERS_COUNT";
    public static final String LAST_SECTION = "LAST_SECTION";
    public static final String CHAPTERS = "CHAPTERS";

    public static final String CHAPTER_INDEX = "CHAPTER_INDEX";
    public static final String SECTION_INDEX = "SECTION_INDEX";
    public static final String IS_BUY = "IS_BUY";
    public static final String WEBVIEW_BASE_URL = "http://ixuea.com/";

    /**
     * 表示，查询用户回答的问题，用户学习的课程，用户学习电子书
     */
    public static final Integer USER_MODEL = 300;
    public static final String URL = "URL";
    public static final String CONTENT = "CONTENT";

    public static final String QQ_KEY = "101481482";
    public static final String ACTION_PLAY = "com.ixuea.courses.mymusic.ACTION_PLAY";
    public static final String ACTION_PREVIOUS = "com.ixuea.courses.mymusic.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.ixuea.courses.mymusic.ACTION_NEXT";
    public static final String ACTION_LIKE = "com.ixuea.courses.mymusic.ACTION_LIKE";
    public static final String ACTION_LYRIC = "com.ixuea.courses.mymusic.ACTION_LYRIC";
    public static final String ACTION_UNLOCK_LYRIC = "com.ixuea.courses.mymusic.ACTION_UNLOCK_LYRIC";

    public static final String SHARE_TEXT_LYRIC_TEMPLATE = "分享%s的单曲《%s》：http://dev-courses-misuc.ixuea.com/songs/%s (来自@我的云音乐)";
    public static final String LYRIC_URL = "http://dev-courses-misuc.ixuea.com/songs/%s";

    /**
     * 我创建的歌单
     */
    public static final String TYPE_MY_CREATE_LIST = "0";

    /**
     * 我收藏的歌单
     */
    public static final String TYPE_MY_LIST_COLLECTION = "10";

    public static final String LIST_ID = "sheet_id";

    /**
     * 评论，排序，最热
     */
    public static final int ORDER_HOT = 10;
    public static final String ORDER = "order";
    public static final String MENTION = "@";
    public static final String HAST_TAG = "#";
    public static final String FILTER = "filter";
    public static final int TYPE_MY_FRIEND = 10;

    /**
     * 值用小写的，好处是，直接就可以传递到服务端
     */
    public static final String NICKNAME = "nickname";
    public static final String AK = "d87b7e285d123411";
    public static final String AS = "1286ef0d6fe8f555";
    public static final String STYLE = "style";
    public static final int STYLE_LIST = 30;
    public static final String VIDEO_ID = "video_id";
    public static final String TOPIC = "topic";
    public static final String OSS_BUCKET_NAME = "dev-courses-misuc";
    public static final int DEFAULT_MESSAGE_COUNT = 10;
    public static final String AVATAR = "AVATAR";
    public static final String ACTION_MESSAGE = "com.ixuea.courses.mymusic.ACTION_MESSAGE";
    public static final String ACTION_MUSIC_PLAYER = "com.ixuea.courses.mymusic.ACTION_MUSIC_PLAYER";
    public static final int DEFAULT_PAGE_SIZE = 10;

}
