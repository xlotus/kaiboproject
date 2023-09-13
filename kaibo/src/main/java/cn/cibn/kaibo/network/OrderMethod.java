package cn.cibn.kaibo.network;

import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.model.ModelOrder;
import cn.cibn.kaibo.model.ModelWrapper;

public class OrderMethod extends BaseMethod {
    private static final String URL_ORDER_LIST = DOMAIN_APP + "api/user/order-list";//订单列表
    private static final String URL_ORDER_CONFIRM = DOMAIN_APP + "api/user/order-confirm";//确认收货

    private static OrderMethod instance = new OrderMethod();

    private OrderMethod() {

    }

    public static OrderMethod getInstance() {
        return instance;
    }

    public ModelWrapper<ModelOrder> reqOrderList(int status, int page, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", UserManager.getInstance().getToken());
        params.put("status", String.valueOf(status));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        return doGet(URL_ORDER_LIST, params, ModelOrder.class);
    }

    public ModelWrapper<String> reqOrderConfirm(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("order_id", orderId);
        params.put("route", "pages/order/order");
        return doGet(URL_ORDER_CONFIRM, params, String.class);
    }

}
