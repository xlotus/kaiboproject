package cn.cibn.kaibo.player;

public enum VideoType {
    LIVE(1), SHORT(2), COMBINED(2);


    final int value;

    VideoType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
