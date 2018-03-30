package pers.nelon.toolkit.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * log工具类
 */
public class L {

    private static String sTag = "";
    private static boolean sEnable = true;

    public static void setGlobalTag(String tag) {
        sTag = tag;
    }

    public static void setEnable(boolean enable) {
        sEnable = enable;
    }

    public static void v() {
        doLog(Log.VERBOSE, null, (Object[]) null);
    }

    public static void v(String str) {
        doLog(Log.VERBOSE, str, (Object[]) null);
    }

    public static void v(Object... objects) {
        doLog(Log.VERBOSE, makeFormatString('v', objects.length), objects);
    }

    public static void v(String str, Object... args) {
        doLog(Log.VERBOSE, str, args);
    }

    public static void d() {
        doLog(Log.DEBUG, null, (Object[]) null);
    }

    public static void d(String str) {
        doLog(Log.DEBUG, str, (Object[]) null);
    }

    public static void d(Object... objects) {
        doLog(Log.DEBUG, makeFormatString('d', objects.length), objects);
    }

    public static void d(String str, Object... args) {
        doLog(Log.DEBUG, str, args);
    }

    public static void i() {
        doLog(Log.INFO, null, (Object[]) null);
    }

    public static void i(String str) {
        doLog(Log.INFO, str, (Object[]) null);
    }

    public static void i(Object... objects) {
        doLog(Log.INFO, makeFormatString('i', objects.length), objects);
    }

    public static void i(String str, Object... args) {
        doLog(Log.INFO, str, args);
    }

    public static void w() {
        doLog(Log.WARN, null, (Object[]) null);
    }

    public static void w(String str) {
        doLog(Log.WARN, str, (Object[]) null);
    }

    public static void w(Object... objects) {
        doLog(Log.WARN, makeFormatString('w', objects.length), objects);
    }

    public static void w(String str, Object... args) {
        doLog(Log.WARN, str, args);
    }

    public static void e() {
        doLog(Log.ERROR, null, (Object[]) null);
    }

    public static void e(String str) {
        doLog(Log.ERROR, str, (Object[]) null);
    }

    public static void e(Object... objects) {
        doLog(Log.ERROR, makeFormatString('e', objects.length), objects);
    }

    public static void e(String str, Object... args) {
        doLog(Log.ERROR, str, args);
    }

    public static void wtf() {
        doLog(Log.ERROR, null, (Object[]) null);
    }

    public static void wtf(String str) {
        doLog(Log.ERROR, str, (Object[]) null);
    }

    public static void wtf(Object... objects) {
        doLog(Log.ERROR, makeFormatString('w', objects.length), objects);
    }

    public static void wtf(String str, Object... args) {
        doLog(Log.ERROR, str, args);
    }

    private static void doLog(int level, String str, Object... args) {
        if (!isEnable()) return;

        StackTraceElement element = new Throwable().fillInStackTrace().getStackTrace()[2];
        String msg = buildMsg(str, args);
        String tag = buildTag(element);

        switch (level) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
        }
    }

    /**
     * 如果sTAG是空则自动从StackTrace中取TAG
     *
     * @param element
     */
    private static String buildTag(StackTraceElement element) {
        if (!TextUtils.isEmpty(sTag)) {
            return sTag;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("[")
                .append("L-")
                .append(element.getClassName())
                .append("#")
                .append(element.getMethodName())
                .append("(")
                .append(element.getFileName())
                .append(":")
                .append(element.getLineNumber())
                .append(")")
                .append("]");
        return stringBuilder.toString();
    }

    private static String buildMsg(String str, Object... args) {
        if (args != null && args.length > 0) {
            str = String.format(str, args);
        }
        if (TextUtils.isEmpty(str)) {
            str = "invoked -- ☑";
        }

        return str;
    }

    private static boolean isEnable() {
        return sEnable;
    }

    private static String makeFormatString(char c, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(c)
                    .append(i)
                    .append("=%s, ");
        }
        try {
            builder.deleteCharAt(builder.length() - 2);
        } catch (Exception ignored) {
        }

        return builder.toString();
    }
}
