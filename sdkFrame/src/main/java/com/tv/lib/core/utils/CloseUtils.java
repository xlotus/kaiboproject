package com.tv.lib.core.utils;

import java.io.Closeable;

public class CloseUtils {

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }
}
