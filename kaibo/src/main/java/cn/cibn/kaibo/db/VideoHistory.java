package cn.cibn.kaibo.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video_history")
public class VideoHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "video_id")
    private String videoId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "cover_img")
    private String cover_img;

    @ColumnInfo(name = "back_img")
    private String back_img;

    @ColumnInfo(name = "anchor_id")
    private String anchor_id;

    @ColumnInfo(name = "play_addr")
    private String play_addr;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "certification_no")
    private String certification_no;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "follow")
    private int follow;

    @ColumnInfo(name = "give")
    private int give;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
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

    public String getCertification_no() {
        return certification_no;
    }

    public void setCertification_no(String certification_no) {
        this.certification_no = certification_no;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getGive() {
        return give;
    }

    public void setGive(int give) {
        this.give = give;
    }
}
