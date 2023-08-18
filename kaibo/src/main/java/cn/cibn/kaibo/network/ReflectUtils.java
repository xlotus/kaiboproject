package cn.cibn.kaibo.network;

import java.lang.reflect.Type;

public class ReflectUtils {

    private static final String TYPE_NAME_PREFIX = "class ";

    public static String getClassName(Type type) {

        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className==null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    public static <T> T newInstance(Type tType) {
        try {
            Class<T> tClass = (Class<T>) getClass(tType);
            return tClass.newInstance();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }
}
