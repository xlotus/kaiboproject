package com.tv.lib.core;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * custom logger class, please use these application wide to replace default android log if possible.
 * features:
 *   level: V/D/I/W/E/A
 *   tag: a string to group and identify logs
 *   filter: include or exclude specified tags
 *   composite: can output logs to multiple appenders
 *   appenders: support android.util.Log and file.
 *   debug version: check is debug version or not
 *   performance issues: 
 *     1. all log is performed in calling thread (so could possible slow).
 *     2. can check if level enabled before prepare log params, see isDebugging()
 */
public final class Logger {

    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
    public static final int A = Log.ASSERT;

    /**
     * logger appender interface.
     */
    public static interface IAppender {
        void println(int level, String tag, String msg);
    }

    /**
     * default appender, based on android.util.Log
     */
    public static final class DefaultAppender implements IAppender {
        private int mLevel;

        public DefaultAppender(int level) {
            mLevel = level;
        }

        @Override
        public void println(int level, String tag, String msg) {
            if (level < mLevel)
                return;
            Log.println(level, tag, msg);
        }
    }


    private static final int MAX_TAG_LENGTH = 23;
    private static String sAppTagPrefix = "AS.";

    private static IAppender mDefaultAppender = null;
    private static List<IAppender> mAdditionalAppenders = null;

    //MUST define two fields to be deceiving proguard
    private static String ORIGINAL_PACKAGE_NAME = "com.tv.lib.core";
    private static String ORIGINAL_CLASS_NAME = "Logger";

    /**
     * whether current app build is debug version or not.
     * note: don't change this field from outside.
     * note: we assume this class is obscured in release version.
     */
    public static boolean isDebugVersion;

    // current logger level, used to filter out too many log outputs in release version.
    private static int mCurrentLevel = Log.INFO;

    // accelerate for get current timestamp (less memory alloc, a little bit quicker)
    // base total millis is 00:00:00 at today.
    private static long baseTotalMillisOfToday;

    private Logger() {}

    // this will add a prefix for all TAG used in this application.
    // eg.
    //    Logger.init("++ ");
    //    then ` adb logcat | grep ++ ` will easily list exactly only your logs
    // NOTE: android TAG max length is 23 chars, so make sure use a short prefix
    public static void init(String tagPrefixOfApp) {
        sAppTagPrefix = tagPrefixOfApp;
        mDefaultAppender = new DefaultAppender(V);

//        initBaseTotalMillisOfToday();

        try {
            // NOTE: here we assume release version will have this Logger class obscured.
            Class.forName(ORIGINAL_PACKAGE_NAME + "." + ORIGINAL_CLASS_NAME);
            isDebugVersion = true;
        } catch (ClassNotFoundException e) {
            isDebugVersion = false;
        }

        if (isDebugVersion)
            mCurrentLevel = V;
        else
            mCurrentLevel = A;

        Logger.e("", "Logger Started, DebugVersion = " + isDebugVersion);
    }

    /**
     * check if currently DEBUG/VERBOSE logs is turned on.
     * caller can check this before some heavy computation on log params.
     * note: this is different with isDebugVersion, log level may changed runtime.
     */
    public static boolean isDebugging() {
        return (mCurrentLevel <= Log.DEBUG);
    }

    public static int getCurrentLevel() {
        return mCurrentLevel;
    }

    public static void setCurrentLevel(int value) {
        mCurrentLevel = value;
    }

    public static void addAppender(IAppender appender) {
        if (mAdditionalAppenders == null)
            mAdditionalAppenders = new CopyOnWriteArrayList<IAppender>();
        mAdditionalAppenders.add(appender);
    }

    public static void removeAppender(IAppender appender) {
        if (mAdditionalAppenders == null)
            return;
        mAdditionalAppenders.remove(appender);
        if (mAdditionalAppenders.isEmpty())
            mAdditionalAppenders = null;
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    public static void write(int level, String category, String format, Object... args) {
        if (level < mCurrentLevel)
            return;
        write(level, category, String.format(Locale.US, format, args));
    }

    public static void write(int level, String category, Throwable tr) {
        if (level < mCurrentLevel)
            return;
        write(level, category, getStackTraceString(tr));
    }

    public static void write(int level, String category, String msg) {
        write(level, category, msg, (Throwable) null);
    }

    public static void write(int level, String category, String msg, Throwable tr) {
        // don't output this log if its level is lower than current level
        if (level < mCurrentLevel)
            return;

        String tag = sAppTagPrefix + category;
        if (tag.length() > MAX_TAG_LENGTH)   // android log doesn't support more than 23 chars in TAG
            tag = tag.substring(0, MAX_TAG_LENGTH);

        String printMsg = "";
        if (tr == null)
            printMsg = String.format(Locale.US, "%s[%d] %s", now(), Thread.currentThread().getId(), msg);
        else
            printMsg = String.format(Locale.US, "%s[%d] %s - %s", now(), Thread.currentThread().getId(), msg, Logger.getStackTraceString(tr));

        if(mDefaultAppender != null)
            mDefaultAppender.println(level, tag, printMsg);

        List<IAppender> appenders = mAdditionalAppenders;
        if (appenders != null) {
            for(IAppender appender : appenders)
                appender.println(level, tag, printMsg);
        }
    }

    @SuppressWarnings("deprecation")
    private static void initBaseTotalMillisOfToday() {
        long totalMillis = System.currentTimeMillis();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(totalMillis);
        baseTotalMillisOfToday = totalMillis - totalMillis % 1000L - ((calendar.get(Calendar.HOUR_OF_DAY) * 60L + calendar.get(Calendar.MINUTE)) * 60L + calendar.get(Calendar.SECOND)) * 1000L;
    }

    // get now as string, it's multiple-thread safe
    private static String now() {
        if (baseTotalMillisOfToday == 0)
            initBaseTotalMillisOfToday();
        long delta = System.currentTimeMillis() - baseTotalMillisOfToday;
        int hours = (int)(delta / (3600L * 1000L)) % 24;
        int minutes = (int)(delta / (60L * 1000L)) % 60;
        int seconds = (int)(delta / 1000L) % 60;
        int millis = (int)(delta % 1000L);
        return String.format(Locale.US, "%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    public static void v(String category, String msg) {
        write(Log.VERBOSE, category, msg);
    }

    public static void v(String category, String msg, Throwable tr) {
        write(Log.VERBOSE, category, msg, tr);
    }

    public static void v(String category, String format, Object... args) {
        write(Log.VERBOSE, category, format, args);
    }

    public static void d(String category, String msg) {
        write(Log.DEBUG, category, msg);
    }

    public static void d(String category, String msg, Throwable tr) {
        write(Log.DEBUG, category, msg, tr);
    }

    public static void d(String category, String format, Object... args) {
        write(Log.DEBUG, category, format, args);
    }

    public static void i(String category, String msg) {
        write(Log.INFO, category, msg);
    }

    public static void i(String category, String format, Object... args) {
        write(Log.INFO, category, format, args);
    }

    public static void w(String category, String msg) {
        write(Log.WARN, category, msg);
    }

    public static void w(String category, Throwable tr) {
        write(Log.WARN, category, tr);
    }

    public static void w(String category, String msg, Throwable tr) {
        write(Log.WARN, category, msg, tr);
    }

    public static void e(String category, String msg) {
        write(Log.ERROR, category, msg);
    }

    public static void e(String category, Throwable tr) {
        write(Log.ERROR, category, tr);
    }

    public static void e(String category, String msg, Throwable tr) {
        write(Log.ERROR, category, msg, tr);
    }

    public static void f(String category, String msg) {
        write(Log.ASSERT, category, msg);
    }

    public static void f(String category, Throwable tr) {
        write(Log.ASSERT, category, tr);
    }

    public static void f(String category, String msg, Throwable tr) {
        write(Log.ASSERT, category, msg, tr);
    }
}
