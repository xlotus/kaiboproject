package cn.cibn.kaibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.cibn.kaibo.model.ModelLive;

public class DataManger {
    private List<ModelLive.Item> liveList;

    private String curLiveId = null;


    private static final DataManger instance = new DataManger();

    private DataManger() {
        liveList = new ArrayList<>();
//        curLivePosition = -1;
    }

    public static DataManger getInstance() {
        return instance;
    }

    public List<ModelLive.Item> getLiveList() {
        return liveList;
    }

    public int getCurLivePosition() {
        if (!liveList.isEmpty()) {
            for (int i = 0; i < liveList.size(); i++) {
                if (Objects.equals(liveList.get(i).getId(), curLiveId)) {
                    return i;
                }
            }
        }
        return 0;
    }

}
