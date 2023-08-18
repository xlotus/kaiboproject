package cn.cibn.kaibo.stat;

import android.os.Build;
import android.text.TextUtils;

import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.device.DeviceHelper;

import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.network.LiveMethod;

public class StatHelper {
    public static void statDeviceInfo() {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        params.put("event", "deviceInfo");
        params.put("deviceModel", Build.MODEL);
        params.put("manufacturer", Build.MANUFACTURER);
        params.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
        if (!TextUtils.isEmpty(DeviceHelper.getMacAddress(ObjectStore.getContext()))) {
            params.put("mac", DeviceHelper.getMacAddress(ObjectStore.getContext()));
        }
        if (!TextUtils.isEmpty(DeviceHelper.getAndroidID(ObjectStore.getContext()))) {
            params.put("androidId", DeviceHelper.getAndroidID(ObjectStore.getContext()));
        }
        if (!TextUtils.isEmpty(DeviceHelper.getIMEI(ObjectStore.getContext()))) {
            params.put("imei", DeviceHelper.getIMEI(ObjectStore.getContext()));
        }
        if (!TextUtils.isEmpty(DeviceHelper.getIMSI(ObjectStore.getContext()))) {
            params.put("imsi", DeviceHelper.getIMSI(ObjectStore.getContext()));
        }
        reportStatInfo(params);
    }

    public static void statEnterLiveRoom(ModelLive.Item item) {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        appendLiveInfo(params, item);
        params.put("event", "enterLiveRoom");
        reportStatInfo(params);
    }

    public static void statExitLiveRoom(ModelLive.Item item) {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        appendLiveInfo(params, item);
        params.put("event", "exitLiveRoom");
        reportStatInfo(params);
    }

    public static void statSwitchUpLiveRoom() {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        params.put("event", "switchUp");
        reportStatInfo(params);
    }

    public static void statSwitchDownLiveRoom() {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        params.put("event", "switchDown");
        reportStatInfo(params);
    }

    public static void statBrowseLiveList() {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        params.put("event", "browseLiveList");
        reportStatInfo(params);
    }

    public static void statBrowseLiveGoods(ModelLive.Item item) {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        appendLiveInfo(params, item);
        params.put("event", "browseLiveGoods");
        reportStatInfo(params);
    }

    public static void statConfirmLiveRoom() {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        params.put("event", "confirm");
        reportStatInfo(params);
    }

    public static void statReturnOperate() {
        Map<String, String> params = new HashMap<>();
        appendCommonParams(params);
        params.put("event", "returnOperate");
        reportStatInfo(params);
    }

    private static void appendCommonParams(Map<String, String> params) {
        params.put("userIdentify", DeviceHelper.getDeviceId(ObjectStore.getContext()));
        params.put("eventTime", String.valueOf(System.currentTimeMillis()));
    }

    private static void appendLiveInfo(Map<String, String> params, ModelLive.Item item) {
        if (item == null) {
            return;
        }
        params.put("liveId", item.getId());
        params.put("anchor", item.getName());
        params.put("authNumber", item.getCertification_no());
    }

    private static void reportStatInfo(Map<String, String> params) {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                LiveMethod.getInstance().reportStat(params);
            }

            @Override
            public void callback(Exception e) {

            }
        });
    }
}
