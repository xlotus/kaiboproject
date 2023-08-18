package cn.cibn.kaibo.data;

import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;

import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.model.ModelGpTimeRange;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;
import cn.cibn.kaibo.settings.LiveSettings;
import cn.cibn.kaibo.utils.DateUtils;

public class ConfigModel {
    private static ConfigModel instance = new ConfigModel();

    private boolean isGrayMode = false;

    private ConfigModel() {

    }

    public static ConfigModel getInstance(){
        return instance;
    }

    public void init() {
        readGpTimeRange();
        reqGpTimeRange();
    }

    public boolean isGrayMode() {
        return isGrayMode;
    }

    private void reqGpTimeRange() {
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
}
