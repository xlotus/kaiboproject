package cn.cibn.kaibo.network;

public class ConnectionException extends Exception {
    private int code;
    private String msg;

    public ConnectionException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
