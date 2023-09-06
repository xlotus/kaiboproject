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
    public MutableLiveData<ModelGoods> goodsLiveData = new MutableLiveData<>();
    public MutableLiveData<String> sellingGoodsId = new MutableLiveData<>();

    private int curPage = Constants.PAGE_FIRST;
    private int loadingPage = -1;

    public void reqFirstPage(String liveId, int type) {
        curPage = Constants.PAGE_FIRST;
        ModelGoods goods = goodsLiveData.getValue();
        if (goods == null) {
            goods = new ModelGoods();
            goods.setList(new ArrayList<>());
        }
        else if (goods.getList() != null) {
            goods.getList().clear();
        }
        goodsLiveData.setValue(goods);

        reqGoodsList(liveId, type, curPage);
    }

    public void reqNextPage(String liveId, int type) {
        int page = curPage + 1;
        if (page == loadingPage) {
            return;
        }
        reqGoodsList(liveId, type, page);
    }

    public void reqGoodsList(String liveId, int type, int page) {
        if (page == loadingPage) {
            return;
        }
        loadingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            private ModelWrapper<ModelGoods> model;
            private String sellingGoodsIdInner;

            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().getGoodsList(liveId, type, page, 1);
                if (model != null && model.isSuccess() && model.getData() != null) {
                    for (ModelGoods.Item item : model.getData().getList()) {
                        if (Objects.equals(item.getIs_sell(), "1")) {
                            sellingGoodsIdInner = item.getGoods_id();
                            break;
                        }
                    }
                }
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    ModelGoods goods = model.getData();
                    curPage = page;

                    ModelGoods tempGoods = goodsLiveData.getValue();
                    if (tempGoods == null) {
                        tempGoods = new ModelGoods();
                    }
                    List<ModelGoods.Item> tempList = tempGoods.getList();
                    if (tempList == null) {
                        tempList = new ArrayList<>();
                    } else if (curPage == Constants.PAGE_FIRST) {
                        tempList.clear();
                    }
                    tempList.addAll(goods.getList());
                    tempGoods.setList(tempList);
                    tempGoods.setRow_count(goods.getRow_count());
                    tempGoods.setMch_qrcode(goods.getMch_qrcode());
                    tempGoods.setType(goods.getType());
                    goodsLiveData.setValue(tempGoods);

                    if (sellingGoodsIdInner != null) {
                        sellingGoodsId.setValue(sellingGoodsIdInner);
                    }
                }
                loadingPage = -1;
            }
        });
    }
}
