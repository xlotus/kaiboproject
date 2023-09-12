package cn.cibn.kaibo.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ModelGuess extends BaseModel implements Serializable {
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
        private String tag;
        private String initials;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return Objects.equals(tag, item.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag);
        }
    }
}
