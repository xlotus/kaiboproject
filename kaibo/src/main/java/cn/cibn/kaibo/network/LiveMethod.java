package cn.cibn.kaibo.network;

import static cn.cibn.kaibo.network.Constants.CODE_SUCCESS;

import com.google.gson.Gson;
import com.tv.lib.core.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.model.BaseModel;
import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.model.ModelGpTimeRange;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelLogin;
import cn.cibn.kaibo.model.ModelQrcode;
import cn.cibn.kaibo.model.ModelStatResponse;
import cn.cibn.kaibo.model.ModelTicket;
import cn.cibn.kaibo.model.ModelUser;
import cn.cibn.kaibo.model.ModelWrapper;

public class LiveMethod extends BaseMethod {

    private static final String URL_LIVE_LIST = DOMAIN + "api/live/live-list?key=a6ab6f3bd1702115c865d1b9acc7d424&source=kumiao";
    private static final String URL_GOODS_LIST = DOMAIN_APP + "api/live/goods-info"; //商品列表
    private static final String URL_QRCODE = DOMAIN_APP + "api/live/get-qrcode";//右下角二维码
    private static final String URL_GP_TIME_RANGE = DOMAIN + "api/default/gp-time-range?key=a6ab6f3bd1702115c865d1b9acc7d424";
//    private static final String URL_GP_TIME_RANGE = "http://liveshop-admin.oeob.net/api/default/gp-time-range?key=a6ab6f3bd1702115c865d1b9acc7d424";
    private static final String URL_STAT = "https://sta.cbnlive.cn?from=sdk"; //统计上报

    private static final String URL_RECOMMEND = DOMAIN_APP + "api/apk/recomm/index";//推荐列表
    private static final String URL_USER_INFO = DOMAIN_APP + "api/user/get-user-info";//个人信息
    private static final String URL_GET_TICKET = DOMAIN_APP + "api/apk/empower/get-ticket"; //获取授权ticket
    private static final String URL_LOGIN = DOMAIN_APP + "api/apk/empower/login";//登录
    private static final String URL_REQ_FOLLOW = DOMAIN_APP + "api/mch/index/shop-follow"; //关注
    private static final String URL_REQ_UN_FOLLOW = DOMAIN_APP + "api/mch/index/un-follow";//取消关注
    private static final String URL_REQ_GIVE = DOMAIN_APP + "api/mch/index/give"; //点赞
    private static final String URL_REQ_CANCEL_GIVE = DOMAIN_APP + "api/mch/index/cancel-give"; //取消点赞

    private static final LiveMethod instance = new LiveMethod();

    private LiveMethod() {

    }

    public static LiveMethod getInstance() {
        return instance;
    }

    public ModelWrapper<ModelQrcode> getQrCode(String liveId, int type) {
        Map<String, String> params = new HashMap<>();
        params.put("id", liveId);
        params.put("type", String.valueOf(type));
        params.put("source", SOURCE);
        return doGet(URL_QRCODE, params, ModelQrcode.class);
    }

    public ModelWrapper<ModelGoods> getGoodsList(String liveId, int type, int page, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("id", liveId);
        params.put("type", String.valueOf(type));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        params.put("source", SOURCE);
        return doGet(URL_GOODS_LIST, params, ModelGoods.class);
    }


    public ModelWrapper<ModelGpTimeRange> getGpTimeRange() {
        return doGet(URL_GP_TIME_RANGE, new HashMap<>(), ModelGpTimeRange.class);
    }

    public ModelWrapper<ModelStatResponse> reportStat(Map<String, String> params) {
        return doGet(URL_STAT, params, ModelStatResponse.class);
    }

    public ModelWrapper<ModelLive> getRecommend(int page, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));;
        params.put("keywork", "");
        return doGet(URL_RECOMMEND, params, ModelLive.class);
    }

    public ModelWrapper<ModelUser> getUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        return doGet(URL_USER_INFO, params, ModelUser.class);
    }

    public ModelWrapper<ModelTicket> getTicket() {
        Map<String, String> params = new HashMap<>();
        return doGet(URL_GET_TICKET, params, ModelTicket.class);
    }

    public ModelWrapper<ModelLogin> reqLogin(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        return doPost(URL_LOGIN, params, ModelLogin.class);
    }

    public ModelWrapper<String> reqFollow(String anchorId) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("anchor_id", anchorId);
        return doPost(URL_REQ_FOLLOW, params, String.class);
    }

    public ModelWrapper<String> reqUnFollow(String anchorId) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("anchor_id", anchorId);
        return doPost(URL_REQ_UN_FOLLOW, params, String.class);
    }

    public ModelWrapper<String> reqGive(String id, int type) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("id", id);
        params.put("type", String.valueOf(type));
        return doGet(URL_REQ_GIVE, params, String.class);
    }

    public ModelWrapper<String> reqCancelGive(String id, int type) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("id", id);
        params.put("type", String.valueOf(type));
        return doGet(URL_REQ_CANCEL_GIVE, params, String.class);
    }
}
