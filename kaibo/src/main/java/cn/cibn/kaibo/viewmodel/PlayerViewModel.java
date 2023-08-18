package cn.cibn.kaibo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.cibn.kaibo.model.ModelLive;

public class PlayerViewModel extends ViewModel {

    public MutableLiveData<Boolean> isInPlay = new MutableLiveData<Boolean>();
    public MutableLiveData<ModelLive.Item> playingVideo = new MutableLiveData<>();

    public boolean isInPlay() {
        return isInPlay.getValue() != null && isInPlay.getValue();
    }

    public void setIsInPlay(boolean isInPlay) {
        this.isInPlay.setValue(isInPlay);
    }
}
