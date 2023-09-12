package cn.cibn.kaiboapp;

import android.app.Application;

import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.ObjectStore;

import cn.cibn.kaiboapp.player.TXCSDKService;

public class KaiboApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectStore.setContext(getApplicationContext());
        Logger.init("[kaibo]");

        TXCSDKService.init(getApplicationContext());
    }
}
