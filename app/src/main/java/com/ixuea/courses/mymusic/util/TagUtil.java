package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.domain.Tag;
import com.ixuea.courses.mymusic.listener.OnTagClickListener;
import com.ixuea.courses.mymusic.span.ClickableSpan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by smile on 2018/6/9.
 */

public class TagUtil {
    /**
     * 匹配@正则：(@.*?)\s，？表示禁用贪婪模式，详细的请参考《详解正则表达式》课程
     * 以\s或者:结尾
     */
    private static final String REG_MENTION = "(@.*?)[\\s|:]";


    private static final String REG_HASH_TAG = "(#.*?#)\\s?";

    /**
     * 从text匹配出Tag
     * @param text
     * @return
     */
    public static java.util.List<Tag> findMentionAndHashTag(String text) {

        //匹配@
        ArrayList<Tag> strings = new ArrayList<>();

        Pattern mention = Pattern.compile(REG_MENTION);
        Matcher mentionMatcher = mention.matcher(text);
        while (mentionMatcher.find()) {
            strings.add(new Tag(mentionMatcher.group(0).trim(),mentionMatcher.start()));
        }

        //@匹配#话题
        Pattern hashTag = Pattern.compile(REG_HASH_TAG);
        Matcher hashTagMatcher = hashTag.matcher(text);
        while (hashTagMatcher.find()) {
            strings.add(new Tag(hashTagMatcher.group(0).trim(),hashTagMatcher.start()));
        }

        return strings;
    }

    /**
     * 对Tag添加点击事件
     * @param text
     * @return
     */
    public static CharSequence process(Context context, String text, final OnTagClickListener onTagClickListener) {
        java.util.List<Tag> mentionAndHashTag = TagUtil.findMentionAndHashTag(text);
        SpannableString spanString = new SpannableString(text);

        for (Tag tag : mentionAndHashTag) {
            //高亮文本
            //ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.text_Highlight));
            int start = tag.getStart();
            final String content = tag.getContent();
            int end = tag.getStart() + content.length();
            //spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //点击事件
            spanString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onTagClickListener.onTagClick(content);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        }
        return spanString;
    }

    /**
     * 对Tag高亮
     * @param text
     * @return
     */
    public static CharSequence processHighlight(Context context, String text) {
        java.util.List<Tag> mentionAndHashTag = TagUtil.findMentionAndHashTag(text);
        SpannableString spanString = new SpannableString(text);

        for (Tag tag : mentionAndHashTag) {
            //高亮文本
            ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.text_Highlight));
            int start = tag.getStart();
            final String content = tag.getContent();
            int end = tag.getStart() + content.length();
            spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return spanString;
    }

    /**
     * 删除Tag
     * @param text
     * @return
     */
    public static String removeTag(String text) {
        return text.replace("#","").replace("@","");
    }
}
