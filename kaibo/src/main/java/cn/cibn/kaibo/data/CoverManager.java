package cn.cibn.kaibo.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.player.PlayerWrapper;

public class CoverManager implements Handler.Callback {
    private static final String TAG = "CoverManager";
    private static final int WHAT_TIMEOUT = 1;
    private static final int TIMEOUT = 5000;

    private static CoverManager instance = new CoverManager();

    private Context context;
    private TXCloudVideoView cloudVideoView;
    private Handler handler;
    private PlayerWrapper player;

    private Map<String, Bitmap> coverMap = new HashMap<>();
    private List<String> loadList = new ArrayList<>();

    private String loadingUrl = null;

    public static CoverManager getInstance() {
        return instance;
    }

    public void init(Context context, TXCloudVideoView cloudVideoView) {
        this.context = context;
        this.cloudVideoView = cloudVideoView;
        this.handler = new Handler(this);
        player = new PlayerWrapper(context);
        player.setPlayerView(cloudVideoView);
        player.getVodPlayer().setAudioPlayoutVolume(0);
    }

    public void destroy() {
        this.context = null;
        this.cloudVideoView = null;
        if (player != null) {
            player.stopPlay();
        }
    }

    public Bitmap getCover(String url) {
        Bitmap bitmap = coverMap.get(url);
        if (bitmap != null) {
            return bitmap;
        }
        tryLoadCover(url);
        return null;
    }

    private void tryLoadCover(String url) {
        if (TextUtils.isEmpty(loadingUrl)) {
            loadCover(url);
        } else {
            if (loadingUrl.equals(url) || loadList.contains(url)) {
                return;
            }
            loadList.add(url);
        }
    }

    private void loadCover(String url) {
        loadingUrl = url;
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(WHAT_TIMEOUT, TIMEOUT);
        snapshot(context, url);
    }

    private void tryLoadNext() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (!loadList.isEmpty()) {
            String url = loadList.remove(0);
            loadCover(url);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_TIMEOUT) {
            tryLoadNext();
            return true;
        }
        return false;
    }

    public void snapshot(Context context, String videoUrl) {
        Logger.d(TAG, "snapshot videoUrl = " + videoUrl);
        player.setVodChangeListener(new PlayerWrapper.OnPlayEventChangedListener() {
            @Override
            public void onProgress(Bundle bundle) {

            }

            @Override
            public void onRcvFirstFrame() {
                Logger.d(TAG, "onRcvFirstFrame");
                player.getVodPlayer().snapshot(new TXLivePlayer.ITXSnapshotListener() {
                    @Override
                    public void onSnapshot(Bitmap bitmap) {
                        Logger.d(TAG, "onSnapshot bitmap = " + bitmap);
                        player.stopPlay();
                        coverMap.put(videoUrl, bitmap);
                        loadingUrl = null;
                        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_COVER_UPDATE);
                        tryLoadNext();
                    }
                });
            }
        });
        // TODO debug
        player.preStartPlay("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
    }
}
