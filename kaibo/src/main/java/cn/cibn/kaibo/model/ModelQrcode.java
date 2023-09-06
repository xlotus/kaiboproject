package cn.cibn.kaibo.model;

public class ModelQrcode extends BaseModel {
    private String qrcode;//推荐二维码地址（如果这个是空的则使用店铺的）
    private String mch_qrcode;


    public String getMch_qrcode() {
        return mch_qrcode;
    }

    public void setMch_qrcode(String mch_qrcode) {
        this.mch_qrcode = mch_qrcode;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
