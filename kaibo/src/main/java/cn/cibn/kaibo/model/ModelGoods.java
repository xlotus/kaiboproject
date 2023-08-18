package cn.cibn.kaibo.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class ModelGoods extends BaseModel {
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
        private String id;
        private String is_sell;
        private String goods_id;
        private String name;
        private String cover_pic;
        private String price;
        private String attr;
        private String original_price;
        private String qrcode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIs_sell() {
            return is_sell;
        }

        public void setIs_sell(String is_sell) {
            this.is_sell = is_sell;
        }

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCover_pic() {
            return cover_pic;
        }

        public void setCover_pic(String cover_pic) {
            this.cover_pic = cover_pic;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAttr() {
            return attr;
        }

        public void setAttr(String attr) {
            this.attr = attr;
        }

        public String getOriginal_price() {
            return original_price;
        }

        public void setOriginal_price(String original_price) {
            this.original_price = original_price;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public int getNum() {
            if (!TextUtils.isEmpty(attr)) {
                try {
                    JSONArray array = new JSONArray(attr);
                    JSONObject json = array.getJSONObject(0);
                    return json.optInt("num");
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
            return 0;
        }
    }
}
