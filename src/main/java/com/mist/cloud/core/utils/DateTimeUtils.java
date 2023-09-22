package com.mist.cloud.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 20:26
 * @Description:
 */
public class DateTimeUtils {
    public static String now(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }
}
