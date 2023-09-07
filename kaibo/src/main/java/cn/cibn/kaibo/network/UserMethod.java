package cn.cibn.kaibo.network;

import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;

public class UserMethod extends BaseMethod {
    private static final String URL_FOLLOW_LIST = DOMAIN_APP + "api/user/follow-list";
    private static final String URL_ANCHOR_INFO = DOMAIN_APP + "api/user/anchor-info";

    private static UserMethod instance = new UserMethod();

    private UserMethod() {

    }

    public static UserMethod getInstance() {
        return instance;
    }

    public ModelWrapper<ModelAnchor> getFollowList(int page, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        return doGet(URL_FOLLOW_LIST, params, ModelAnchor.class);
    }

    public ModelWrapper<ModelLive> getAnchorInfo(String anchorId, int page, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("anchor_id", anchorId);
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        return doGet(URL_ANCHOR_INFO, params, ModelLive.class);
    }
}
