package cn.cibn.kaibo.utils;

import android.view.View;

public class FocusUtils {
    public static void scaleLeft(View view) {
        view.setScaleX(1.1f);
        view.setScaleY(1.1f);
        view.setTranslationX(-(int)(view.getWidth() * 0.05));
    }

    public static int scaleRight(View view) {
        return scaleRight(view, 0);
    }

    public static int scaleRight(View view, int offsetX) {
        view.setScaleX(1.1f);
        view.setScaleY(1.1f);
        int translationX = (int)(view.getWidth() * 0.05) + offsetX;
        view.setTranslationX(translationX);
        return translationX;
    }

    public static void resetScale(View view) {
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setTranslationX(0);
        view.setTranslationY(0);
    }
}
