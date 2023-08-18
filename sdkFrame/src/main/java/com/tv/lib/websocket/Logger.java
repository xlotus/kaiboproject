package com.tv.lib.websocket;

public class Logger {
    private static Logger instance = new Logger();

    private Logger() {

    }

    public static Logger getInstance() {
        return instance;
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public void trace(Object content) {
        if (content != null) {
            com.tv.lib.core.Logger.d("", content.toString());
        }
    }

    public void trace(Object... content) {
        if (content != null) {
            StringBuilder builder = new StringBuilder();
            for (Object obj : content) {
                if (obj != null) {
                    builder.append(obj.toString());
                }
            }
            com.tv.lib.core.Logger.d("", builder.toString());
        }
    }

    public void error(Object content) {
        if (content != null) {
            com.tv.lib.core.Logger.e("", content.toString());
        }
    }

    public void error(Object... content) {
        if (content != null) {
            StringBuilder builder = new StringBuilder();
            for (Object obj : content) {
                if (obj != null) {
                    builder.append(obj.toString());
                }
            }
            com.tv.lib.core.Logger.e("", builder.toString());
        }
    }
}
