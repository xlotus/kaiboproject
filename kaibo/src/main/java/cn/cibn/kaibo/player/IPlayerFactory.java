package cn.cibn.kaibo.player;

import android.content.Context;

public interface IPlayerFactory {
    BaseVideoView createVideoView(Context context);
}
