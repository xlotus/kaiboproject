package cn.cibn.kaibo.utils;

import com.tv.lib.core.change.ChangeListenerManager;

import cn.cibn.kaibo.change.ChangedKeys;

public class ToastUtils {
    public static void showToast(String text) {
        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_SHOW_TOAST, text);
    }
}
