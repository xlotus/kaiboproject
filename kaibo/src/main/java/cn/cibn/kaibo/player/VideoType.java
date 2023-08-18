package cn.cibn.kaibo.player;

public enum VideoType {
    SHORT(1), COMBINED(2), LIVE(3);


    final int value;

    VideoType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
