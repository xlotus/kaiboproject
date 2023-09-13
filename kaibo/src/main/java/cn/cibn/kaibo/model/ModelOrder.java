package cn.cibn.kaibo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelOrder extends BaseModel {
    private List<Item> list;
    private int row_count;
    private int page_count;

    public List<Item> getList() {
        return list;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }

    public int getRow_count() {
        return row_count;
    }

    public void setRow_count(int row_count) {
        this.row_count = row_count;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public static class Item implements Serializable {
        @SerializedName("order_no")
        private String orderNo;

        @SerializedName("order_refund_id")
        private String orderRefundId;

        @SerializedName("pay_price")
        private String payPrice;

        @SerializedName("refund_type")
        private String refundType;

        @SerializedName("refund_status")
        private int refundStatus;

        @SerializedName("consignee")
        private String consignee;

        @SerializedName("refund_price")
        private int refundPrice;

        @SerializedName("is_user_send")
        private String isUserSend;

        @SerializedName("goods_list")
        private List<GoodsListItem> goodsList;

        @SerializedName("mch")
        private Mch mch;

        @SerializedName("addtime")
        private String addtime;

        @SerializedName("qr_code")
        private String qrCode;

        @SerializedName("or_type")
        private String orType;

        @SerializedName("is_agree")
        private int isAgree;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("total_price")
        private String totalPrice;

        @SerializedName("is_pay")
        private int isPay;

        @SerializedName("is_send")
        private int isSend;

        @SerializedName("is_confirm")
        private int isConfirm;

        @SerializedName("is_comment")
        private int isComment;

        @SerializedName("is_cancel")
        private int isCancel;

        @SerializedName("is_revoke")
        private int isRevoke;

        public String getOrderNo(){
            return orderNo;
        }

        public String getOrderRefundId(){
            return orderRefundId;
        }

        public String getPayPrice(){
            return payPrice;
        }

        public String getRefundType(){
            return refundType;
        }

        public int getRefundStatus(){
            return refundStatus;
        }

        public String getConsignee(){
            return consignee;
        }

        public int getRefundPrice(){
            return refundPrice;
        }

        public String getIsUserSend(){
            return isUserSend;
        }

        public List<GoodsListItem> getGoodsList(){
            return goodsList;
        }

        public Mch getMch(){
            return mch;
        }

        public String getAddtime(){
            return addtime;
        }

        public String getQrCode(){
            return qrCode;
        }

        public String getOrType(){
            return orType;
        }

        public int getIsAgree(){
            return isAgree;
        }

        public String getOrderId(){
            return orderId;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public int getIsPay() {
            return isPay;
        }

        public void setIsPay(int isPay) {
            this.isPay = isPay;
        }

        public int getIsSend() {
            return isSend;
        }

        public void setIsSend(int isSend) {
            this.isSend = isSend;
        }

        public int getIsConfirm() {
            return isConfirm;
        }

        public void setIsConfirm(int isConfirm) {
            this.isConfirm = isConfirm;
        }

        public int getIsComment() {
            return isComment;
        }

        public void setIsComment(int isComment) {
            this.isComment = isComment;
        }

        public int getIsCancel() {
            return isCancel;
        }

        public void setIsCancel(int isCancel) {
            this.isCancel = isCancel;
        }

        public int getIsRevoke() {
            return isRevoke;
        }

        public void setIsRevoke(int isRevoke) {
            this.isRevoke = isRevoke;
        }
    }

    public static class GoodsListItem{

        @SerializedName("goods_name")
        private String goodsName;

        @SerializedName("attr_list")
        private List<AttrListItem> attrList;

        @SerializedName("goods_pic")
        private String goodsPic;

        @SerializedName("price")
        private String price;

        @SerializedName("num")
        private int num;

        @SerializedName("goods_id")
        private int goodsId;

        public String getGoodsName(){
            return goodsName;
        }

        public List<AttrListItem> getAttrList(){
            return attrList;
        }

        public String getGoodsPic(){
            return goodsPic;
        }

        public String getPrice(){
            return price;
        }

        public int getNum(){
            return num;
        }

        public int getGoodsId(){
            return goodsId;
        }
    }

    public static class Mch{

        @SerializedName("name")
        private String name;

        @SerializedName("logo")
        private String logo;

        @SerializedName("id")
        private int id;

        public String getName(){
            return name;
        }

        public String getLogo(){
            return logo;
        }

        public int getId(){
            return id;
        }
    }

    public static class AttrListItem{

        @SerializedName("attr_group_name")
        private String attrGroupName;

        @SerializedName("attr_group_id")
        private String attrGroupId;

        @SerializedName("attr_name")
        private String attrName;

        @SerializedName("attr_id")
        private String attrId;

        public String getAttrGroupName(){
            return attrGroupName;
        }

        public String getAttrGroupId(){
            return attrGroupId;
        }

        public String getAttrName(){
            return attrName;
        }

        public String getAttrId(){
            return attrId;
        }
    }
}
