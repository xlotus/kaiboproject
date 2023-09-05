package cn.cibn.kaibo.model;

public class ModelUser extends BaseModel {
    private String id;
    private String nickname;
    private String user_key;
    private String avatar_url;
    private OrderCount order_count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public OrderCount getOrder_count() {
        return order_count;
    }

    public void setOrder_count(OrderCount order_count) {
        this.order_count = order_count;
    }

    public static class OrderCount {
        private int all;
        private int status_0;
        private int status_1;
        private int status_2;
        private int status_3;
        private int status_4;
        private int status_5;

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }

        public int getStatus_0() {
            return status_0;
        }

        public void setStatus_0(int status_0) {
            this.status_0 = status_0;
        }

        public int getStatus_1() {
            return status_1;
        }

        public void setStatus_1(int status_1) {
            this.status_1 = status_1;
        }

        public int getStatus_2() {
            return status_2;
        }

        public void setStatus_2(int status_2) {
            this.status_2 = status_2;
        }

        public int getStatus_3() {
            return status_3;
        }

        public void setStatus_3(int status_3) {
            this.status_3 = status_3;
        }

        public int getStatus_4() {
            return status_4;
        }

        public void setStatus_4(int status_4) {
            this.status_4 = status_4;
        }

        public int getStatus_5() {
            return status_5;
        }

        public void setStatus_5(int status_5) {
            this.status_5 = status_5;
        }
    }
}
