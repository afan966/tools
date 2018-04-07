package com.afan.tool.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类
 * @author afan
 *
 */
public class DateUtil {

	/** 默认的日期格式 */
	public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** 锁对象 */
    private static final Object lockObj = new Object();

    /** 存放不同的日期模板格式的sdf的Map */
    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
    
    /** 打点记录时间戳 */
    private static final ThreadLocal<Long> signMillis = new ThreadLocal<Long>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat format() {
    	return format(FORMAT);
    }
    private static SimpleDateFormat format(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    System.out.println("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }
    
    /**
     * 打标，配合used使用
     */
    public static void sign() {
    	signMillis.set(System.currentTimeMillis());
    }
    /**
     * 距离时间，配合sign使用
     */
	public static long used() {
		Long signTime = signMillis.get();
		if (signTime != null && signTime.longValue() > 0) {
			return System.currentTimeMillis() - signTime;
		}
		sign();
		return 0;
	}
	/**
	 * 距离时间，格式化输出
	 * @return
	 */
	public static String usedStr() {
		Long signTime = signMillis.get();
		if (signTime != null && signTime.longValue() > 0) {
			return timeSlot(System.currentTimeMillis() - signTime);
		}
		sign();
		return timeSlot(0);
	}
    
    //时间段格式化
    public static String timeSlot(long t) {
		StringBuilder slot = new StringBuilder();
		int hour = 0, min = 0, sec = 0, minSec = 0;
		if (t > 0) {
			minSec = (int)(t % 1000);
			t = t / 1000;
			if (t > 0) {
				sec = (int)(t % 60);
				t = t / 60;
				if (t > 0) {
					min = (int)(t % 60);
					hour = (int)(t / 60);
				}
			}
			if (hour > 0) {
				slot.append(hour).append("h");
			}
			if (min > 0) {
				slot.append(min).append("m");
			}
			if (sec > 0) {
				slot.append(sec).append("s");
			}
			if (minSec > 0) {
				slot.append(minSec).append("ms");
			}
		}
		return "0ms";
    }
    /**
     * 获取时间
     * @return
     */
	public static Date getDate() {
		return new Date();
	}

	public static Date getDate(long time) {
		return new Date(time);
	}

	public static Date getDate(java.sql.Date date) {
		return new Date(date.getTime());
	}

	public static Date getDate(java.sql.Timestamp date) {
		return new Date(date.getTime());
	}

	public static Date getDate(String date) {
		try {
			return format().parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getDate(String date, String format) {
		try {
			return format(format).parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getDate(int year, int month, int day, int hour, int minute, int second, int milliSecond) {
		Calendar c = Calendar.getInstance();
		c.setTime(getDate());
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, milliSecond);
		return c.getTime();
	}
	
	/**
	 * 格式化日期
	 * @return
	 */
	public static String getDateStr() {
		return format().format(getDate());
	}

	public static String getDateStr(Date date) {
		return format().format(date);
	}

	public static String getDateStr(Date date, String format) {
		return format(format).format(date);
	}

	public static String getDateStr(long time) {
		return format().format(getDate(time));
	}

	public static String getDateStr(long time, String format) {
		return format(format).format(getDate(time));
	}

	public static java.sql.Date getSqlDate() {
		return new java.sql.Date(System.currentTimeMillis());
	}

	public static java.sql.Date getSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Timestamp getSqlTimestamp() {
		return new java.sql.Timestamp(System.currentTimeMillis());
	}

	public static java.sql.Timestamp getSqlTimestamp(Date date) {
		return new java.sql.Timestamp(date.getTime());
	}
	
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	
	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinute(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MINUTE);
	}
	
	public static int getSecond(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.SECOND);
	}
	//星期日到星期6返回1~7
	public static int getWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getCurrentYear() {
		return getYear(getDate());
	}
	
	public static int getCurrentMonth() {
		return getMonth(getDate());
	}

	public static int getCurrentDay() {
		return getDay(getDate());
	}

	public static int getCurrentHour() {
		return getHour(getDate());
	}
	
	public static int getCurrentMinute() {
		return getMinute(getDate());
	}
	
	public static int getCurrentSecond() {
		return getSecond(getDate());
	}
	
	public static int getCurrentWeek() {
		return getWeek(getDate());
	}
	
	public static Date setYear(Date date, int year) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.YEAR, year);
		return c.getTime();
	}
	
	public static Date setMonth(Date date, int month) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MONTH, month-1);
		return c.getTime();
	}

	public static Date setDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}

	public static Date setHour(Date date, int hour) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, hour);
		return c.getTime();
	}
	
	public static Date setMinute(Date date, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MINUTE, minute);
		return c.getTime();
	}
	
	public static Date setSecond(Date date, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}
	
	public static Date addYear(Date date, int year) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, year);
		return c.getTime();
	}
	
	public static Date addMonth(Date date, int month) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, month);
		return c.getTime();
	}

	public static Date addDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}

	public static Date addHour(Date date, int hour) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, hour);
		return c.getTime();
	}
	
	public static Date addMinute(Date date, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minute);
		return c.getTime();
	}
	
	public static Date addSecond(Date date, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, second);
		return c.getTime();
	}
	
	public static Date addMilliSecond(Date date, int milliSecond) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MILLISECOND, milliSecond);
		return c.getTime();
	}
	
	/**
	 * 当天0点
	 * @return
	 */
	public static Date getDayFirst() {
		return getDayFirst(getDate());
	}
	
	public static Date getDayFirst(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	/**
	 * 当天23:59:59
	 * @return
	 */
	public static Date getDayLast() {
		return getDayLast(getDate());
	}
	
	public static Date getDayLast(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	/**
	 * 当月1号0点
	 * @return
	 */
	public static Date getMonthFirst() {
		return getMonthFirst(getDate());
	}
	
	public static Date getMonthFirst(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static void main(String[] args) {
		System.out.println(getDateStr(getDate(2016, 12, 1, 14, 30, 0,0)));
		System.out.println(getWeek(getDate(2016, 12, 1, 0, 0, 0,0)));
		System.out.println(getDateStr(getDayLast()));
	}

}