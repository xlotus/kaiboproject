package cn.cibn.kaibo.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import cn.cibn.kaibo.player.VideoType;

public class ModelConfig extends BaseModel implements Serializable {

    private int is_open;//是否开启自定义
    private int top_guide;//上方指引提示间隔 秒
    private int right_guide;//右侧指引提示间隔 秒
    private int under_guide; //下方指引提示间隔 秒
    private int left_guide;//左侧指引提示间隔 秒
    private String top_guide_url;//上方指引图片
    private String right_guide_url;//右侧指引图片
    private String under_guide_url;//下方指引图片
    private String left_guide_url;//左侧指引图片
    private String goods_frame_url;//商品二维码边框
    private String bg_url; //apk背景图片 加载慢时显示此图片
    private int is_grey; //是否置灰 0否 1是 如果开启 整个apk主题颜色为灰色
    private String about;//关于我们

    public int getIs_open() {
        return is_open;
    }

    public void setIs_open(int is_open) {
        this.is_open = is_open;
    }

    public int getTop_guide() {
        return top_guide;
    }

    public void setTop_guide(int top_guide) {
        this.top_guide = top_guide;
    }

    public int getRight_guide() {
        return right_guide;
    }

    public void setRight_guide(int right_guide) {
        this.right_guide = right_guide;
    }

    public int getUnder_guide() {
        return under_guide;
    }

    public void setUnder_guide(int under_guide) {
        this.under_guide = under_guide;
    }

    public int getLeft_guide() {
        return left_guide;
    }

    public void setLeft_guide(int left_guide) {
        this.left_guide = left_guide;
    }

    public String getTop_guide_url() {
        return top_guide_url;
    }

    public void setTop_guide_url(String top_guide_url) {
        this.top_guide_url = top_guide_url;
    }

    public String getRight_guide_url() {
        return right_guide_url;
    }

    public void setRight_guide_url(String right_guide_url) {
        this.right_guide_url = right_guide_url;
    }

    public String getUnder_guide_url() {
        return under_guide_url;
    }

    public void setUnder_guide_url(String under_guide_url) {
        this.under_guide_url = under_guide_url;
    }

    public String getLeft_guide_url() {
        return left_guide_url;
    }

    public void setLeft_guide_url(String left_guide_url) {
        this.left_guide_url = left_guide_url;
    }

    public String getGoods_frame_url() {
        return goods_frame_url;
    }

    public void setGoods_frame_url(String goods_frame_url) {
        this.goods_frame_url = goods_frame_url;
    }

    public String getBg_url() {
        return bg_url;
    }

    public void setBg_url(String bg_url) {
        this.bg_url = bg_url;
    }

    public int getIs_grey() {
        return is_grey;
    }

    public void setIs_grey(int is_grey) {
        this.is_grey = is_grey;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
