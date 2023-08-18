package cn.cibn.kaibo.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import cn.cibn.kaibo.player.VideoType;

public class ModelLive extends BaseModel implements Serializable {
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
        private String title;
        private String cover_img;
        private String back_img;
        private String anchor_id;
        private String play_addr;
        private String name;
        private String header_bg;

        private String certification_no;

        private int type = VideoType.LIVE.getValue();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover_img() {
            return cover_img;
        }

        public void setCover_img(String cover_img) {
            this.cover_img = cover_img;
        }

        public String getBack_img() {
            return back_img;
        }

        public void setBack_img(String back_img) {
            this.back_img = back_img;
        }

        public String getAnchor_id() {
            return anchor_id;
        }

        public void setAnchor_id(String anchor_id) {
            this.anchor_id = anchor_id;
        }

        public String getPlay_addr() {
            return play_addr;
        }

        public void setPlay_addr(String play_addr) {
            this.play_addr = play_addr;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHeader_bg() {
            return header_bg;
        }

        public void setHeader_bg(String header_bg) {
            this.header_bg = header_bg;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Item && ((Item) obj).id.equals(id)) {
                return true;
            }
            return false;
        }

        public String getCertification_no() {
            return certification_no;
        }

        public void setCertification_no(String certification_no) {
            this.certification_no = certification_no;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", cover_img='" + cover_img + '\'' +
                    ", back_img='" + back_img + '\'' +
                    ", anchor_id='" + anchor_id + '\'' +
                    ", play_addr='" + play_addr + '\'' +
                    ", name='" + name + '\'' +
                    ", header_bg='" + header_bg + '\'' +
                    ", certification_no='" + certification_no + '\'' +
                    '}';
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
