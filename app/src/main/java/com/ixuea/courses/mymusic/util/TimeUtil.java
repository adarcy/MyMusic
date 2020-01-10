package com.ixuea.courses.mymusic.util;


import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Created by smile on 17/1/24.
 */

public class TimeUtil {

  public static int parseInteger(String timeString) {
    timeString = timeString.replace(":", ".");
    timeString = timeString.replace(".", "@");
    String[] timeData = timeString.split("@");
    if (timeData.length == 3) {
      int m = Integer.parseInt(timeData[0]);
      int s = Integer.parseInt(timeData[1]);
      int ms = Integer.parseInt(timeData[2]);
      int currTime = (m * 60 + s) * 1000 + ms;
      return currTime;
    } else {
      return 0;
    }
  }

  public static String parseString(int time) {
    time /= 1000;
    int minute = time / 60;
    int second = time % 60;
    minute %= 60;
    return String
        .format("%02d:%02d", new Object[]{Integer.valueOf(minute), Integer.valueOf(second)});
  }

  /**
   * 将毫秒格式化为分:秒，例如：150:11
   * @param time
   * @return
   */
  public static String formatMSTime(int time) {
    if (time == 0) {
      return "00:00";
    }
    time /= 1000;
    int i = time / 60;
    return String.format("%02d", i) + ":"
        + String.format("%02d", time - (i * 60));
  }

  /**
   * 将ISO8601格式转为yyyy-MM-dd HH:mm
   * @param date
   * @return
   */
  public static String dateTimeFormat1(String date) {
    try {
      DateTime dateTime = new DateTime(date);
      return dateTime.toString("yyyy-MM-dd HH:mm");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   //        一小时以内显示几分钟前；
   //        24小时内显示几小时前；
   //        超过24小时三天内（含三天）显示几天前；
   //        3天以上显示具体日期，到天即可。
   //        超过一年显示年份，年内不显示年份
   * @param
   * @return
   */
  public static String toLocalDate(long time){

    //现在的时间
    Integer nowYear =  Integer.parseInt(DateTime.now().toString("yyyy"));

    //时间格式化
    //org.joda.time.format.DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");

    //传进来的时间解析
    DateTime paraDate = new DateTime(time);//年月日 时分秒
    Integer paraYear = Integer.parseInt(paraDate.toString("yyyy"));//年
    String paraDay = paraDate.toString("yyyy-MM-dd");


    DateTime nowDate = new DateTime().now();   //当前
    Interval hours1 = new Interval(nowDate.minusHours(1), nowDate);
    Interval hours24 = new Interval(nowDate.minusHours(24), nowDate);
    Interval day = new Interval(nowDate.minusDays(4), nowDate);
    Interval years = new Interval(nowDate.minusYears(1), nowDate);


    if (hours1.contains(paraDate)) { //一小时以内显示几分钟前；
      Interval minute  = new  Interval(paraDate, nowDate);
      int res = minute.toPeriod().getMinutes();
      if(res == 0 || res == 1 ){
        return "刚刚";
      }
      return res+"分钟前";
      // return "几分钟前";
    } else if (hours24.contains(paraDate)) { //24小时内显示几小时前；
      Interval hours  = new  Interval(paraDate, nowDate);
      int res = hours.toPeriod().getHours();
      return res+"小时前";
      //return "几小时前";
    } else if (day.contains(paraDate)) { //超过24小时三天内（含三天）显示几天前；
      Interval Day  = new  Interval(paraDate, nowDate);
      int res = Day.toPeriod().getDays();
      return res+"天前";
    }else if(paraYear < nowYear ){
      return paraDay;
    }else {
      return paraDate.toString("MM-dd");
    }
  }
}
