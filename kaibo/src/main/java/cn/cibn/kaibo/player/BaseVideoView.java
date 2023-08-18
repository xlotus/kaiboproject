package cn.cibn.kaibo.player;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public abstract class BaseVideoView extends FrameLayout implements IPlayer {
    public BaseVideoView(@NonNull Context context) {
        super(context);
    }
}
