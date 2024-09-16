package com.lts.context;

public class userContext {
    private static ThreadLocal<Long> userThread = new ThreadLocal<>();

    public static void setUserId(Long id) {
        userThread.set(id);
    }

    public static Long getUserId() {
        return userThread.get();
    }

    public static void clear() {
        userThread.remove();
    }
}
