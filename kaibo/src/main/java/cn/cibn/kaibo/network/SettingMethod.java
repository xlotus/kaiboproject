package cn.cibn.kaibo.network;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cibn.kaibo.model.ModelConfig;
import cn.cibn.kaibo.model.ModelQuestions;
import cn.cibn.kaibo.model.ModelWrapper;

public class SettingMethod extends BaseMethod {
    private static final String URL_QUESTIONS = DOMAIN_APP + "api/default/apk-question";//常见问题列表
    private static final String URL_SERVER_CONFIG = DOMAIN_APP + "api/default/apk-config";//获取apk配置信息

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
}
