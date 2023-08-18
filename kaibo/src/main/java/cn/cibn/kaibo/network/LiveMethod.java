package cn.cibn.kaibo.network;

import static cn.cibn.kaibo.network.Constants.CODE_SUCCESS;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.model.BaseModel;
import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.model.ModelGpTimeRange;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelStatResponse;
import cn.cibn.kaibo.model.ModelWrapper;

public class LiveMethod extends Connection {



    private static final String DOMAIN = "https://api.cbnlive.cn/";
//    private static final String DOMAIN = "http://liveshop-admin.oeob.net/";

    private static final String URL_LIVE_LIST = DOMAIN + "api/live/live-list?key=a6ab6f3bd1702115c865d1b9acc7d424&source=kumiao";
    private static final String URL_GOODS_LIST = DOMAIN + "api/live/live-goods-list?key=a6ab6f3bd1702115c865d1b9acc7d424&source=kumiao";
    private static final String URL_QRCODE = DOMAIN + "api/live/live-qrcode?key=a6ab6f3bd1702115c865d1b9acc7d424&source=kumiao";
    private static final String URL_GP_TIME_RANGE = DOMAIN + "api/default/gp-time-range?key=a6ab6f3bd1702115c865d1b9acc7d424";
//    private static final String URL_GP_TIME_RANGE = "http://liveshop-admin.oeob.net/api/default/gp-time-range?key=a6ab6f3bd1702115c865d1b9acc7d424";
    private static final String URL_STAT = "https://sta.cbnlive.cn?from=sdk";
    private static final LiveMethod instance = new LiveMethod();

    private LiveMethod() {

    }

    public static LiveMethod getInstance() {
        return instance;
    }

    private <T extends BaseModel> ModelWrapper<T> realConnect(String method, String url, Type tType) {
        ModelWrapper<T> wrapper = new ModelWrapper<>();
        try {
            Object content = connect(method, url);
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
//            e.printStackTrace();
        }
        return wrapper;
    }

    private <T extends BaseModel> ModelWrapper<T> doGet(String url, Map<String, String> params, Type tType) {
        String fullUrl = buildParams(url, params);
        return realConnect("GET", fullUrl, tType);
    }

    public ModelWrapper<ModelLive> getLiveList(int page, int size) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(size));
        params.put("page", String.valueOf(page));
        return doGet(URL_LIVE_LIST, params, ModelLive.class);


//        ModelLive modelLive = new ModelLive();
//        modelLive.setPage_count(page);
//        modelLive.setRow_count(31);
//
//        List<ModelLive.Item> list = new ArrayList<>();
//        if (page == 1) {
//            for (int i = 0; i < 10; i++) {
//                ModelLive.Item item = new ModelLive.Item();
//                item.setId(i + "");
//                item.setTitle("title");
//                item.setName("name");
//                list.add(item);
//            }
//        } else {
//            ModelLive.Item item = new ModelLive.Item();
//            item.setId("1007842");
//            item.setTitle("title-1007842");
//            item.setName("name-1007842");
//            list.add(item);
//        }
//        modelLive.setList(list);
//        ModelWrapper<ModelLive> wrapper = new ModelWrapper<>();
//        wrapper.setData(modelLive);
//        wrapper.setCode(200);
//        return wrapper;
    }

    public ModelWrapper<ModelGoods> getGoodsList(String liveId, int page) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", "10");
        params.put("page", String.valueOf(page));
        params.put("live_id", liveId);
        return doGet(URL_GOODS_LIST, params, ModelGoods.class);
    }


    public ModelWrapper<ModelGpTimeRange> getGpTimeRange() {
        return doGet(URL_GP_TIME_RANGE, new HashMap<>(), ModelGpTimeRange.class);
    }

    public ModelWrapper<ModelStatResponse> reportStat(Map<String, String> params) {
        return doGet(URL_STAT, params, ModelStatResponse.class);
    }
}
