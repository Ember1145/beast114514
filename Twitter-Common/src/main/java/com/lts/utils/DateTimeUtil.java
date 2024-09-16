package com.lts.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;

public class DateTimeUtil {

    // 将LocalDateTime转换为时间戳的方法
    public static long toLong(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // 将时间戳转换为LocalDateTime的方法
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}