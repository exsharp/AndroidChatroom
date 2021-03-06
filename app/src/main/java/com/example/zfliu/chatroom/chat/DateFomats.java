package com.example.zfliu.chatroom.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sharp on 2/2/2015.
 */
public class DateFomats {


    /** 转换获取出入的字符串时间值  */
    public static String getStringTime(String strTime){
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd"+"\0\0"+"HH:"+"mm");
        long sTime = Long.valueOf(strTime);
        return sd.format(new Date(sTime * 1000));
    }


    /** 获取并格式化当前时间值  */
    public static String getCurrentTime(long date){
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd"+"\t"+"HH:"+"mm:"+"ss");
        return sd.format(date);
    }
}