package com.itchen.db.router;

public class DBContextHolder {
    //使用ThreadLocal来保存上下文的变量信息，保证存储进去的信息只能被当前的线程读取到，并且线程之间不会受到影响，主要有两个作用：
    private static final ThreadLocal<String> dbKey = new ThreadLocal<>();
    private static final ThreadLocal<String> tbKey = new ThreadLocal<>();

    public static String getDBKey() {
        return dbKey.get();
    }
    public static String getTBKey() {
        return tbKey.get();
    }

    public static void setDBKey(String dbKeyName) {
        dbKey.set(dbKeyName);
    }
    public static void setTBKey(String tbKeyName) {
        tbKey.set(tbKeyName);
    }
    public static void clearDBKey() {
        dbKey.remove();
    }
    public static void clearTBKey() {
        tbKey.remove();
    }
}
