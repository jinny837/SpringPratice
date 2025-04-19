package com.basic.springpratice.common.util;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtil {
    private DateUtil() {}

    // 기본 포맷
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String format(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }
    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    public static LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    public static LocalDateTime plusDays(LocalDateTime dt, long days) {
        return dt.plusDays(days);
    }
    public static long betweenDays(LocalDate d1, LocalDate d2) {
        return ChronoUnit.DAYS.between(d1, d2);
    }
    // … 필요에 따라 추가 메서드 더 구현
}