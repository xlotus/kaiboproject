package cn.cibn.kaibo.ui.video;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.cibn.kaibo.model.ModelLive;

public abstract class VideoListDataSource implements Serializable {
    public static final int PAGE_FIRST = 1;

    protected List<ModelLive.Item> liveList = new ArrayList<>();
    private String curLiveId = null;
    //    private int pageCount = PAGE_FIRST;
    private int requestingPage = -1;

    private OnReadyListener onReadyListener;

    public List<ModelLive.Item> getLiveList() {
        return liveList;
    }

    public final String getCurLiveId() {
        return curLiveId;
    }

    public final void setCurLiveId(String curLiveId) {
        this.curLiveId = curLiveId;
    }

    public void setOnReadyListener(OnReadyListener listener) {
        onReadyListener = listener;
    }

    public void clear() {
        liveList.clear();
        curLiveId = null;
        requestingPage = -1;
    }

    public final ModelLive.Item getPreLiveItem() {
        if (liveList != null && !TextUtils.isEmpty(curLiveId)) {
            int i = 0;
            for (; i < liveList.size(); i++) {
                if (Objects.equals(liveList.get(i).getId(), curLiveId)) {
                    break;
                }
            }
            if (i > 0) {
                --i;
            } else {
                i = liveList.size() - 1;
            }
            return liveList.get(i);
        }
        return null;
    }

    public final ModelLive.Item getNextLiveItem() {
        if (liveList != null && !TextUtils.isEmpty(curLiveId)) {
            int i = liveList.size() - 1;
            for (; i >= 0; i--) {
                if (Objects.equals(liveList.get(i).getId(), curLiveId)) {
                    break;
                }
            }
            if (i < liveList.size() - 1) {
                ++i;
            } else {
                i = 0;
            }
            return liveList.get(i);
        }
        return null;
    }

    protected final void updateLiveList(ModelLive modelLive) {
        liveList.clear();
        liveList.addAll(modelLive.getList());
        if (onReadyListener != null) {
            onReadyListener.onSourceReady();
        }
    }

    public abstract void reqLiveList();

    public interface OnReadyListener {
        void onSourceReady();
    }
}
