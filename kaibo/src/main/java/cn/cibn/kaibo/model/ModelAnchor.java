package cn.cibn.kaibo.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ModelAnchor extends BaseModel implements Serializable {
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
        private String anchor_id;//主播id
        private String cover_img;//直播间头像
        private String name;//主播名称
        private String certification_no;//认证编号
        private int fans;//粉丝数量

        public String getAnchor_id() {
            return anchor_id;
        }

        public void setAnchor_id(String anchor_id) {
            this.anchor_id = anchor_id;
        }

        public String getCover_img() {
            return cover_img;
        }

        public void setCover_img(String cover_img) {
            this.cover_img = cover_img;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCertification_no() {
            return certification_no;
        }

        public void setCertification_no(String certification_no) {
            this.certification_no = certification_no;
        }

        public int getFans() {
            return fans;
        }

        public void setFans(int fans) {
            this.fans = fans;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return Objects.equals(anchor_id, item.anchor_id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(anchor_id);
        }
    }
}
