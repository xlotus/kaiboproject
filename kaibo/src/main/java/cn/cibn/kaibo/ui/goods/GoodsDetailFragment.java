package cn.cibn.kaibo.ui.goods;

import android.view.LayoutInflater;

import com.tv.lib.core.Logger;

import java.util.ArrayList;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.databinding.FragmentGoodsDetailBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class GoodsDetailFragment extends KbBaseFragment<FragmentGoodsDetailBinding> {
    private static final String TAG = "GoodsDetailFragment";


    public static GoodsDetailFragment createInstance() {
        Logger.d(TAG, "createInstance");
        return new GoodsDetailFragment();
    }

    @Override
    protected FragmentGoodsDetailBinding createBinding(LayoutInflater inflater) {
        return FragmentGoodsDetailBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        Logger.d(TAG, "initView");

        binding.tvGoodsListTitle.setText(mContext.getResources().getString(R.string.goods_list_title, 0));

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
    }

    @Override
    public void onListenerChange(String key, Object data) {

        super.onListenerChange(key, data);
    }

    @Override
    protected void updateView() {
        Logger.d(TAG, "updateView");

    }

    @Override
    protected void onActivityCreated() {
        super.onActivityCreated();

    }

    public void requestFocus() {

    }

}
