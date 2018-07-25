package com.cdc.cdccmc.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期辅助类
 * @author ZhuWen
 * @date 2017-12-28
 */
public class DateUtil {
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String yyyy_MM_dd_HH_mm_ss_ZH = "yyyy年MM月dd日HH时mm分ss秒";
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	/**
	 * yyyyMMdd
	 */
	public static final String yyyyMMdd = "yyyyMMdd";
	/**
	 * yyMMdd
	 */
	public static final String yyMMdd = "yyMMdd";
	/**
	 * yyyy-MM-dd
	 */
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	/**
	 * 获取今天的yyyyMMddHHmmss格式的日期
	 */
	public static String today_yyyyMMddHHmmss() {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddHHmmss);
		return sdf.format(new Date());
	}
	/**
	 * 获取今天的yyyyMMddHHmmss格式的日期(中文)
	 */
	public static String today_yyyyMMddHHmmssZH() {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_ZH);
		return sdf.format(new Date());
	}

	/**
	 * 获取今天的yyyyMMdd格式的日期
	 */
	public static String today_yyyyMMdd() {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMdd);
		return sdf.format(new Date());
	}
	/**
	 * 获取今天的yyMMdd格式的日期
	 */
	public static String today_yyMMdd() {
		SimpleDateFormat sdf = new SimpleDateFormat(yyMMdd);
		return sdf.format(new Date());
	}
	/**
	 * 获取今天的yyyy_MM_dd格式的日期
	 */
	public static String today_yyyy_MM_dd() {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd);
		return sdf.format(new Date());
	}

	/**
	 * 日期格式化
	 * @param formatStr 日期格式字符串
	 * @param date 日期对象
	 * @return 返回日期格式化后的字符串
	 */
	public static String format(String formatStr, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		return sdf.format(date);
	}
	/**
	 * 日期格式化
	 * @param formatStr 日期格式字符串
	 * @param date 日期对象
	 * @return 返回日期格式化后的字符串
	 */
	public static String format(String formatStr, Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		return sdf.format(timestamp);
	}

	/**
	 * 日期字符串转换为Timestamp日期对象
	 * @param formatStr
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Timestamp parseToTimestamp(String dateStr,String formatStr) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = sdf.parse(dateStr);
		return new Timestamp(date.getTime());
	}

	/**
	 * 日期字符串转换为Timestamp日期对象
	 * @param formatStr
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseToDate(String dateStr,String formatStr) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		return sdf.parse(dateStr);
	}
	/**
	 *
	 * @param time1
	 * @param time2
	 * @return 返回time2 - time1的间隔天数
	 * @throws ParseException
	 */
	public static int daysBetween(Timestamp time1,Timestamp time2) throws ParseException{
       long between_days=(time2.getTime()-time1.getTime())/(1000*3600*24);
       return Integer.parseInt(String.valueOf(between_days));
    }

	/**
	 * @return 返回当前时间+2秒的时间
	 */
	public static Timestamp currentAddTwoSecond(){
		return new Timestamp(new Date().getTime()+2000);
	}

	/**
	 * @return 返回当前时间+1秒的时间
	 */
	public static Timestamp currentAddFourSecond(){
		return new Timestamp(new Date().getTime()+4000);
	}

	/**
	 * @return 返回当前时间
	 */
	public static Timestamp currentTimestamp(){
		return new Timestamp(new Date().getTime());
	}

	/**
	 * @return 返回当前时间少2秒
	 */
	public static Timestamp currentTimestampLess2Second(){
		return new Timestamp(new Date().getTime() - 2000);
	}
}
