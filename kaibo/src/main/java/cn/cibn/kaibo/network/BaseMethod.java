package cn.cibn.kaibo.network;

import static cn.cibn.kaibo.network.Constants.CODE_SUCCESS;

import com.google.gson.Gson;
import com.tv.lib.core.Logger;

import java.lang.reflect.Type;
import java.util.Map;

import cn.cibn.kaibo.model.ModelWrapper;

public class BaseMethod extends Connection {
    protected static final String TAG = "BaseMethod";
    protected static final String DOMAIN = "https://api.cbnlive.cn/";
//    private static final String DOMAIN = "http://liveshop-admin.oeob.net/";

    protected static final String DOMAIN_APP = "http://bd.cbnlive.net/";
//    private static final String DOMAIN_APP = "http://liveshop-admin.oeob.net/";


    private <T> ModelWrapper<T> realConnect(String method, String url, Map<String, String> params, Type tType) {
        ModelWrapper<T> wrapper = new ModelWrapper<>();
        try {
            Object content = connect(method, url, params);
            if (content != null) {
//                T model = ReflectUtils.newInstance(tType);
//                if (model != null) {
//                    model.parseFromJson(new JSONObject(content.toString()));
//                    wrapper.setData(model);
//                }
                wrapper.setData(new Gson().fromJson(content.toString(), tType));
            }
            wrapper.setCode(CODE_SUCCESS);
        } catch (ConnectionException e) {
            wrapper.setCode(e.getCode());
            wrapper.setErrMsg(e.getMsg());
        } catch (Exception e) {
            wrapper.setCode(Constants.CODE_FAIL);
            Logger.d(TAG, url, e);
        }
        return wrapper;
    }

    protected <T> ModelWrapper<T> doGet(String url, Map<String, String> params, Type tType) {
        return realConnect(GET, url, params, tType);
    }

    protected <T> ModelWrapper<T> doPost(String url, Map<String, String> params, Type tType) {
        return realConnect(POST, url, params, tType);
    }

}
