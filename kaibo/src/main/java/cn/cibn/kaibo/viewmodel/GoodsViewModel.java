package cn.cibn.kaibo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tv.lib.core.lang.thread.TaskHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.Constants;
import cn.cibn.kaibo.network.LiveMethod;

public class GoodsViewModel extends ViewModel {
    public MutableLiveData<List<ModelGoods.Item>> goodsList = new MutableLiveData<>();
    public MutableLiveData<Integer> total = new MutableLiveData<>();
    public MutableLiveData<String> sellingGoodsId = new MutableLiveData<>();

    private int curPage = Constants.PAGE_FIRST;
    private int loadingPage = -1;

    public void reqFirstPage(String liveId) {
        curPage = Constants.PAGE_FIRST;
        List<ModelGoods.Item> goods = goodsList.getValue();
        if (goods != null) {
            goods.clear();
        }
        goodsList.setValue(goods);
        total.setValue(0);

        reqGoodsList(liveId, curPage);
    }

    public void reqNextPage(String liveId) {

    }

    public void reqGoodsList(String liveId, int page) {
        if (page == loadingPage) {
            return;
        }
        loadingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            private ModelWrapper<ModelGoods> model;

            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().getGoodsList(liveId, page);
                if (model != null && model.isSuccess() && model.getData() != null) {
                    for (ModelGoods.Item item : model.getData().getList()) {
                        if (Objects.equals(item.getIs_sell(), "1")) {
                            sellingGoodsId.setValue(item.getGoods_id());
                            break;
                        }
                    }
                }
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    ModelGoods goods = model.getData();
                    total.setValue(goods.getRow_count());
                    curPage = goods.getPage_count();

                    List<ModelGoods.Item> tempList = goodsList.getValue();
                    if (tempList == null) {
                        tempList = new ArrayList<>();
                    } else if (curPage == Constants.PAGE_FIRST) {
                        tempList.clear();
                    }
                    tempList.addAll(goods.getList());
                    goodsList.setValue(tempList);
                }
                loadingPage = -1;
            }
        });
    }
}
