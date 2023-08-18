package cn.cibn.kaibo;

import android.text.TextUtils;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.model.ModelGpTimeRange;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;
import cn.cibn.kaibo.settings.LiveSettings;
import cn.cibn.kaibo.utils.DateUtils;

public class DataManger {
    public static final int PAGE_FIRST = 1;

//    private String defaultLiveId = "";
    private int defaultLivePosition = -1;

    private List<ModelLive.Item> liveList;
//    private int liveListTotal = 0;
//    private int curLivePosition = -1;
    private String curLiveId = null;
//    private int pageCount = PAGE_FIRST;
    private int requestingPage = -1;

    private String sellingGoodsId;

    private boolean isGrayMode = false;

    private static final DataManger instance = new DataManger();

    private DataManger() {
        liveList = new ArrayList<>();
//        curLivePosition = -1;
    }

    public static DataManger getInstance() {
        return instance;
    }

    public void init() {
        readGpTimeRange();
    }

    public void clear() {
        liveList.clear();
//        liveListTotal = 0;
//        curLivePosition = -1;
        curLiveId = null;
//        pageCount = PAGE_FIRST;
        requestingPage = -1;
        sellingGoodsId = "";
//        defaultLiveId = "";
        defaultLivePosition = -1;
    }

    public List<ModelLive.Item> getLiveList() {
        return liveList;
    }

    public int getCurLivePosition() {
        if (!liveList.isEmpty()) {
            for (int i = 0; i < liveList.size(); i++) {
                if (Objects.equals(liveList.get(i).getId(), curLiveId)) {
                    return i;
                }
            }
        }
        return 0;
//        return curLivePosition;
    }

//    public void setCurLivePosition(int curLivePosition) {
//        this.curLivePosition = curLivePosition;
//    }

    public int indexOfLive(ModelLive.Item item) {
        if (!liveList.isEmpty()) {
            try {
                return liveList.indexOf(item);
            }
            catch (Exception e) {
//                e.printStackTrace();
            }

        }
        return -1;
    }

    public ModelLive.Item getLiveItemById(String id) {
        if (liveList != null && !TextUtils.isEmpty(id)) {
            ModelLive.Item item;
            for (int i = 0; i < liveList.size(); i++) {
                item = liveList.get(i);
                if (Objects.equals(item.getId(), id)) {
                    return item;
                }
            }
        }
        return null;
    }

    public ModelLive.Item getDefaultLiveItem() {
        if (liveList.isEmpty()) {
            return null;
        }
        ModelLive.Item item = liveList.get(0);
        curLiveId = item.getId();
        return item;
    }

    public ModelLive.Item getCurrentLiveItem() {
//        if (liveList.isEmpty() || curLivePosition < 0 || curLivePosition >= liveList.size()) {
//            return null;
//        }
//        return liveList.get(curLivePosition);
        return getLiveItemById(curLiveId);
    }

    public ModelLive.Item getPreLiveItem() {
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

//        if (liveList.isEmpty()) {
//            return null;
//        }
//        int nextPosition = curLivePosition;
//        if (curLivePosition > 0) {
//            nextPosition = curLivePosition - 1;
//        }
//        return liveList.get(nextPosition);
    }

    public ModelLive.Item getNextLiveItem() {
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
//        if (liveList.isEmpty()) {
//            return null;
//        }
//        int nextPosition = curLivePosition;
//        if (nextPosition < liveList.size() - 1) {
//            nextPosition = curLivePosition + 1;
//        }
//
//        return liveList.get(nextPosition);
    }

    public void updateLiveList(ModelLive modelLive) {
//        if (modelLive.getPage_count() == PAGE_FIRST) {
            liveList.clear();
//        }
        liveList.addAll(modelLive.getList());
//        liveListTotal = modelLive.getRow_count();
//        pageCount = modelLive.getPage_count();
    }

    public void reqLiveList(int page) {
        if (page == requestingPage) {
            return;
        }
        requestingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            ModelLive modelLive;

            @Override
            public void execute() throws Exception {
                Thread.sleep(1000);
                int p = PAGE_FIRST;
                while (true) {
                    ModelWrapper<ModelLive> model = LiveMethod.getInstance().getLiveList(p, 10);
                    if (!model.isSuccess() || model.getData() == null) {
                        break;
                    }
                    if (p == PAGE_FIRST) {
                        modelLive = model.getData();
                    } else if (modelLive != null) {
                        modelLive.getList().addAll(model.getData().getList());
                        modelLive.setRow_count(model.getData().getRow_count());
                        modelLive.setPage_count(model.getData().getPage_count());
                    }
                    if (modelLive.getList().size() >= modelLive.getRow_count()) {
                        break;
                    }
                    p++;
                }
//                int innerPage = page;
//                if (innerPage == PAGE_FIRST && !TextUtils.isEmpty(defaultLiveId)) {
//                    while (true) {
//                        ModelWrapper<ModelLive> model = LiveMethod.getInstance().getLiveList(innerPage);
//                        if (!model.isSuccess() || model.getData() == null) {
//                            break;
//                        }
//                        for (int i = 0; i < model.getData().getList().size(); i++) {
//                            if (Objects.equals(model.getData().getList().get(i).getId(), defaultLiveId)) {
//                                defaultLivePosition = (modelLive != null ? modelLive.getList().size() : 0) + i;
//                                break;
//                            }
//                        }
//                        if (modelLive != null) {
//                            modelLive.getList().addAll(model.getData().getList());
//                            modelLive.setRow_count(model.getData().getRow_count());
//                            modelLive.setPage_count(model.getData().getPage_count());
//                        } else {
//                            modelLive = model.getData();
//                        }
//
//                        if (modelLive.getList().size() >= modelLive.getRow_count()) {
//                            break;
//                        }
//                        if (defaultLivePosition >= 0) {
//                            break;
//                        }
//                        innerPage = innerPage + 1;
//                        requestingPage = innerPage;
//                    }
//                } else {
//                    modelLive = LiveMethod.getInstance().getLiveList(page).getData();
//                }


//                Thread.sleep(1000);
//                model = new ModelWrapper<>();
//                model.setCode(200);
//                ModelLive modelLive = new ModelLive();
//                modelLive.setPage_count(page);
//                modelLive.setRow_count(100);
//                List<ModelLive.Item> dataList = new ArrayList<>();
//                for (int i = 0; i < 10; i++) {
//                    ModelLive.Item live = new ModelLive.Item();
//                    int id = page * 10 + i;
//                    live.setId("" + id);
//                    live.setCover_img("http://imgduoshanghu.oeob.net/web/uploads/image/store_1/872b8a80d1fad1e252db4ee8c207b7f27ea25b47.png");
//                    live.setTitle("live " + id);
//                    live.setName("anchor" + id);
//                    dataList.add(live);
//                }
//                modelLive.setList(dataList);
//                model.setData(modelLive);

            }

            @Override
            public void callback(Exception e) {
                if (modelLive != null) {
//                    for (ModelLive.Item item : modelLive.getList()) {
//                        item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
//                    }
                    updateLiveList(modelLive);
                }
                ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_LIVE_LIST_UPDATE);
                requestingPage = -1;
            }
        });
    }

    public void reqGpTimeRange() {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelGpTimeRange> model;

            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().getGpTimeRange();
//                model.setCode(200);
//                ModelGpTimeRange d = new ModelGpTimeRange();
//                d.setGp_time_start("2023-04-26 15:00:20");
//                d.setGp_time_end("2023-04-30 17:00:22");
//                model.setData(d);
//                Thread.sleep(5000);
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
//                    model.getData().setGp_time_start("");
//                    model.getData().setGp_time_end("");
                    saveGpTimeRange(model.getData());
                    boolean temp = isGrayMode;
                    isGrayMode = DateUtils.isInRange(model.getData().getGp_time_start(), model.getData().getGp_time_end());
                    if (isGrayMode != temp) {
                        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_GRAY_MODE);
                    }
                }
            }
        });
    }

    private void saveGpTimeRange(ModelGpTimeRange model) {
        if (model != null) {
            LiveSettings.setGpTimeStart(model.getGp_time_start());
            LiveSettings.setGpTimeEnd(model.getGp_time_end());
        }
    }

    private void readGpTimeRange() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                String gpStartTime = LiveSettings.getGpTimeStart();
                String gpTimeEnd = LiveSettings.getGpTimeEnd();
                isGrayMode = DateUtils.isInRange(gpStartTime, gpTimeEnd);
            }

            @Override
            public void callback(Exception e) {
                if (isGrayMode) {
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_GRAY_MODE);
                }
            }
        });
    }

    public String getSellingGoodsId() {
        return sellingGoodsId;
    }

    public void setSellingGoodsId(String sellingGoodsId) {
        this.sellingGoodsId = sellingGoodsId;
    }

//    public int getLiveListTotal() {
//        return liveListTotal;
//    }

//    public int getPageCount() {
//        return pageCount;
//    }

    public boolean isGrayMode() {
        return isGrayMode;
    }

//    public void setDefaultLiveId(String defaultLiveId) {
//        this.defaultLiveId = defaultLiveId;
//    }

//    public boolean foundDefaultLiveId() {
//        return TextUtils.isEmpty(defaultLiveId) || defaultLivePosition >= 0;
//    }

    public String getCurLiveId() {
        return curLiveId;
    }

    public void setCurLiveId(String curLiveId) {
        this.curLiveId = curLiveId;
    }
}
