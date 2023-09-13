package cn.cibn.kaibo.network;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.model.ModelConfig;
import cn.cibn.kaibo.model.ModelOption;
import cn.cibn.kaibo.model.ModelQuestions;
import cn.cibn.kaibo.model.ModelVersion;
import cn.cibn.kaibo.model.ModelWrapper;

public class SettingMethod extends BaseMethod {
    private static final String URL_QUESTIONS = DOMAIN_APP + "api/default/apk-question";//常见问题列表
    private static final String URL_SERVER_CONFIG = DOMAIN_APP + "api/default/apk-config";//获取apk配置信息
    private static final String URL_OPTION_LIST = DOMAIN_APP + "api/default/opinion-list";//意见列表
    private static final String URL_FEEDBACK = DOMAIN_APP + "api/user/feedback";//提交意见反馈

    private static final String URL_VERSION = DOMAIN_APP + "api/default/version";//获取最新版本信息

    private static SettingMethod instance = new SettingMethod();

    private SettingMethod() {

    }

    public static SettingMethod getInstance() {
        return instance;
    }

    public ModelWrapper<List<ModelQuestions>> reqQuestions() {
        Map<String, String> params = new HashMap<>();
        return doGet(URL_QUESTIONS, params, new TypeToken<List<ModelQuestions>>() {}.getType());
    }

    public ModelWrapper<ModelConfig> reqServerConfig() {
        Map<String, String> params = new HashMap<>();
        return doGet(URL_SERVER_CONFIG, params, ModelConfig.class);
    }

    public ModelWrapper<List<ModelOption>> reqOptionList() {
        Map<String, String> params = new HashMap<>();
        return doGet(URL_OPTION_LIST, params, new TypeToken<List<ModelOption>>() {}.getType());
    }

    public ModelWrapper<String> reqFeedback(int optionId, String deviceId, String deviceModel, String utdid, String version) {
        Map<String, String> params = new HashMap<>();
        params.put("opinion_id", String.valueOf(optionId));
        params.put("device_uuid", deviceId);
        params.put("device_model", deviceModel);
        params.put("utdid", utdid);
        params.put("version_no", version);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("access_token", UserManager.getInstance().getToken());
        String url = buildParams(URL_FEEDBACK, queryParams);
        return doPost(url, params, ModelConfig.class);
    }

    public ModelWrapper<ModelVersion.Item> reqCheckVersion() {
        Map<String, String> params = new HashMap<>();
        return doGet(URL_VERSION, params, ModelVersion.Item.class);
    }
}
