package cn.cibn.kaibo.utils;

import android.text.TextUtils;

import com.meituan.android.walle.WalleChannelReader;
import com.tv.lib.core.lang.ObjectStore;

public class ParamsHelper {
    private static final String DEFAULT_CHANNEL = "platform";
    public static String getChannel() {
        String channel = WalleChannelReader.getChannel(ObjectStore.getContext());
        if (TextUtils.isEmpty(channel)) {
            return DEFAULT_CHANNEL;
        }
        return channel;
    }
}
