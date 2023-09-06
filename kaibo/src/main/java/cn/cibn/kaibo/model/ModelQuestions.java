package cn.cibn.kaibo.model;

import java.io.Serializable;
import java.util.List;

public class ModelQuestions extends BaseModel implements Serializable {
    private String id;
    private String title;
    private String answer;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
