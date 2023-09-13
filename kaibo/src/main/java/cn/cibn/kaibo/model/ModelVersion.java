package cn.cibn.kaibo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelVersion extends BaseModel implements Serializable {
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
        @SerializedName("store_id")
        private String storeId;

        @SerializedName("update_time")
        private long updateTime;

        @SerializedName("file")
        private String file;

        @SerializedName("is_force")
        private String isForce;

        @SerializedName("channel")
        private String channel;

        @SerializedName("version_number")
        private String versionNumber;

        @SerializedName("id")
        private String id;

        @SerializedName("add_time")
        private long addTime;

        @SerializedName("channel_id")
        private String channelId;

        @SerializedName("file_size")
        private String fileSize;

        @SerializedName("content")
        private String content;

        @SerializedName("is_delete")
        private String isDelete;

        public String getStoreId(){
            return storeId;
        }

        public long getUpdateTime(){
            return updateTime;
        }

        public String getFile(){
            return file;
        }

        public String getIsForce(){
            return isForce;
        }

        public String getChannel(){
            return channel;
        }

        public String getVersionNumber(){
            return versionNumber;
        }

        public String getId(){
            return id;
        }

        public long getAddTime(){
            return addTime;
        }

        public String getChannelId(){
            return channelId;
        }

        public String getFileSize(){
            return fileSize;
        }

        public String getContent(){
            return content;
        }

        public String getIsDelete(){
            return isDelete;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public void setIsForce(String isForce) {
            this.isForce = isForce;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }
    }
}
