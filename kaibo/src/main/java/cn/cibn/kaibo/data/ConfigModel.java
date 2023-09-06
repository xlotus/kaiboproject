package cn.cibn.kaibo.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;

import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.model.ModelConfig;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.SettingMethod;
import cn.cibn.kaibo.settings.LiveSettings;

public class ConfigModel {
    private static ConfigModel instance = new ConfigModel();
    private ModelConfig modelConfig;

    private ConfigModel() {

    }

    public static ConfigModel getInstance(){
        return instance;
    }

    public void init() {
        reqReadLocalConfig();
//        readGpTimeRange();
//        reqGpTimeRange();
    }

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public boolean isGrayMode() {
        return modelConfig != null && modelConfig.getIs_grey() == 1 && false;
    }

    private void reqReadLocalConfig() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                String configStr = LiveSettings.getServerConfig();
                if (!TextUtils.isEmpty(configStr)) {
                    modelConfig = new Gson().fromJson(configStr, ModelConfig.class);
                }
            }

            @Override
            public void callback(Exception e) {
                reqServerConfig();
            }
        });
    }

    private void reqServerConfig() {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelConfig> model;

            @Override
            public void execute() throws Exception {
                boolean temp = isGrayMode();
                model = SettingMethod.getInstance().reqServerConfig();
                if (model != null && model.isSuccess() && model.getData() != null) {
                    LiveSettings.setServerConfig(new Gson().toJson(model.getData()));
                    if (temp != isGrayMode()) {
                        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_GRAY_MODE);
                    }
                }
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess()) {
                    modelConfig = model.getData();
                }
            }
        });
    }

//    private void reqGpTimeRange() {
//        TaskHelper.exec(new TaskHelper.Task() {
//            ModelWrapper<ModelGpTimeRange> model;
//
//            @Override
//            public void execute() throws Exception {
//                model = LiveMethod.getInstance().getGpTimeRange();
//            }
//
//            @Override
//            public void callback(Exception e) {
//                if (model != null && model.isSuccess() && model.getData() != null) {
//                    saveGpTimeRange(model.getData());
//                    boolean temp = isGrayMode;
//                    isGrayMode = DateUtils.isInRange(model.getData().getGp_time_start(), model.getData().getGp_time_end());
//                    if (isGrayMode != temp) {
//                        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_GRAY_MODE);
//                    }
//                }
//            }
//        });
//    }
//
//    private void saveGpTimeRange(ModelGpTimeRange model) {
//        if (model != null) {
//            LiveSettings.setGpTimeStart(model.getGp_time_start());
//            LiveSettings.setGpTimeEnd(model.getGp_time_end());
//        }
//    }
//
//    private void readGpTimeRange() {
//        TaskHelper.exec(new TaskHelper.Task() {
//            @Override
//            public void execute() throws Exception {
//                String gpStartTime = LiveSettings.getGpTimeStart();
//                String gpTimeEnd = LiveSettings.getGpTimeEnd();
//                isGrayMode = DateUtils.isInRange(gpStartTime, gpTimeEnd);
//            }
//
//            @Override
//            public void callback(Exception e) {
//                if (isGrayMode) {
//                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_GRAY_MODE);
//                }
//            }
//        });
//    }
}
