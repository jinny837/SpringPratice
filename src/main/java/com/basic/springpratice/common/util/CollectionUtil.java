package com.basic.springpratice.common.util;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CollectionUtil {
    private CollectionUtil() {}

    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty();
    }
    public static <T> List<T> emptyIfNull(Collection<T> c) {
        return c == null ? Collections.emptyList() : new ArrayList<>(c);
    }
    public static <T> List<T> filter(Collection<T> c, Predicate<T> p) {
        if (c == null) return Collections.emptyList();
        return c.stream().filter(p).collect(Collectors.toList());
    }
    // 페이징 처리 헬퍼
    public static <T> List<T> page(List<T> list, int page, int size) {
        int from = page * size;
        if (from >= list.size()) return Collections.emptyList();
        return list.subList(from, Math.min(from + size, list.size()));
    }
}