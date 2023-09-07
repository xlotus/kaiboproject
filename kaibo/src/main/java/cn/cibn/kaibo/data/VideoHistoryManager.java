package cn.cibn.kaibo.data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.tv.lib.core.lang.thread.TaskHelper;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.db.AppDatabase;
import cn.cibn.kaibo.db.VideoHistory;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.UserMethod;

public class VideoHistoryManager implements Handler.Callback {
    private static final int WHAT_UPLOAD = 1;
    private static final int UPLOAD_INTERVAL = 100000;
    private static VideoHistoryManager instance = new VideoHistoryManager();

    private Context context;
    private Handler handler;

    private VideoHistoryManager() {

    }

    public static VideoHistoryManager getInstance() {
        return instance;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_UPLOAD) {
            tryUpload();
            return true;
        }
        return false;
    }

    public void init(Context context) {
        this.context = context;
        handler = new Handler(this);
        tryUpload();
    }

    public void addHistory(ModelLive.Item liveItem) {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                AppDatabase.getInstance(context).videoHistoryDao().deleteByVideoId(liveItem.getId());
                VideoHistory history = new VideoHistory();
                history.setVideoId(liveItem.getId());
                history.setTitle(liveItem.getTitle());
                history.setCover_img(liveItem.getCover_img());
                history.setBack_img(liveItem.getBack_img());
                history.setAnchor_id(liveItem.getAnchor_id());
                history.setPlay_addr(liveItem.getPlay_addr());
                history.setName(liveItem.getName());
                history.setCertification_no(liveItem.getCertification_no());
                history.setType(liveItem.getType());
                history.setFollow(liveItem.getFollow());
                history.setGive(liveItem.getGive());
                AppDatabase.getInstance(context).videoHistoryDao().insert(history);
            }

            @Override
            public void callback(Exception e) {

            }
        });
    }

    private void tryUpload() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                List<VideoHistory> historyList = AppDatabase.getInstance(context).videoHistoryDao().getAll();
                if (historyList != null && !historyList.isEmpty()) {
                    List<UploadItem> dataList = new ArrayList<>();
                    for (int i = 0; i < historyList.size(); i++) {
                        VideoHistory history = historyList.get(i);
                        dataList.add(new UploadItem(history.getVideoId(), history.getType()));
                    }
                    String history = new Gson().toJson(dataList);
                    ModelWrapper<String> model = UserMethod.getInstance().pushHistory(history);
                    if (model != null && model.isSuccess()) {
                        AppDatabase.getInstance(context).videoHistoryDao().clear();
                    }
                }
            }

            @Override
            public void callback(Exception e) {
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(WHAT_UPLOAD, UPLOAD_INTERVAL);
                }
            }
        });
    }

    private static class UploadItem {
        @SerializedName("id")
        private String id;
        @SerializedName("type")
        private int type;

        public UploadItem(String id, int type) {
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
