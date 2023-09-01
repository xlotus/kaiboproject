package cn.cibn.kaibo.model;

public class OrderAction {
    public static final OrderAction CONFIRM_RECEIPT = new OrderAction(1, "确认收货");
    public static final OrderAction VIEW_LOGISTICS = new OrderAction(2, "查看物流");
    public static final OrderAction PAY = new OrderAction(3, "立即付款");
    public static final OrderAction RETURN = new OrderAction(4, "寄回商品");

    private int id;
    private String actionName;

    private OrderAction(int id, String actionName) {
        this.id = id;
        this.actionName = actionName;
    }

    public int getId() {
        return id;
    }

    public String getActionName() {
        return actionName;
    }
}
