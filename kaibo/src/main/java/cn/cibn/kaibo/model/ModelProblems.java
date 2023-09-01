package cn.cibn.kaibo.model;

import java.io.Serializable;
import java.util.List;

public class ModelProblems extends BaseModel implements Serializable {
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
        private String problemTitle;
        private String problemAnswer;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProblemTitle() {
            return problemTitle;
        }

        public void setProblemTitle(String problemTitle) {
            this.problemTitle = problemTitle;
        }

        public String getProblemAnswer() {
            return problemAnswer;
        }

        public void setProblemAnswer(String problemAnswer) {
            this.problemAnswer = problemAnswer;
        }
    }
}
