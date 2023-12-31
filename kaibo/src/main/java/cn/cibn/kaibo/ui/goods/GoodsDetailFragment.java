package cn.cibn.kaibo.ui.goods;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentGoodsDetailBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.player.VideoType;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.viewmodel.GoodsViewModel;

/**
 * 作为第一级抽屉时，数据源来自于GoodsViewModel中的goodsList
 * 作为第二级抽屉时，数据源来自于商品列表传递
 */
public class GoodsDetailFragment extends KbBaseFragment<FragmentGoodsDetailBinding> {
    private static final String TAG = "GoodsDetailFragment";

    private int grade; //默认0 第一级抽屉， 1 第二级抽屉

    private ModelGoods.Item goods;

    private GoodsViewModel goodsViewModel;

    public static GoodsDetailFragment createInstance(int grade) {
        Logger.d(TAG, "createInstance");
        GoodsDetailFragment fragment = new GoodsDetailFragment();
        Bundle args = new Bundle();
        args.putInt("grade", grade);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentGoodsDetailBinding createBinding(LayoutInflater inflater) {
        return FragmentGoodsDetailBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        Logger.d(TAG, "initView");

        if (grade == 0) {
            binding.layoutGoodsDetailRoot.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_first_bg));
            binding.layoutPressBackToCloseGoodsDetail.getRoot().setVisibility(View.VISIBLE);
            if (goodsViewModel != null) {
                goodsViewModel.goodsLiveData.observe(getViewLifecycleOwner(), new Observer<ModelGoods>() {
                    @Override
                    public void onChanged(ModelGoods modelGoods) {
                        if (modelGoods == null || modelGoods.getType() == VideoType.LIVE.getValue()) {
                            return;
                        }
                        if (modelGoods.getList() != null && modelGoods.getList().size() > 0) {
                            goods = modelGoods.getList().get(0);
                            updateView();
                        }
                    }
                });
            }
        } else if (grade == 1) {
            binding.layoutGoodsDetailRoot.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_second_bg));
            binding.layoutPressBackToCloseGoodsDetail.getRoot().setVisibility(View.GONE);
        }

        getParentFragmentManager().setFragmentResultListener("goods", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                goods = (ModelGoods.Item) result.getSerializable("goodsDetail");
                updateView();
            }
        });
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
        if (binding == null) {
            return;
        }
        boolean isGrayMode = ConfigModel.getInstance().isGrayMode();
        if (isGrayMode) {
            binding.ivGoodsQrcode.setBackgroundResource(R.drawable.bg_qrcode_goods_gray);
            binding.tvGoodsPrice.setTextColor(getResources().getColor(R.color.color_mode_gray));
            binding.tvGoodsPriceSign.setTextColor(getResources().getColor(R.color.color_mode_gray));
        } else {
            binding.ivGoodsQrcode.setBackgroundResource(R.drawable.bg_qrcode_goods);
            binding.tvGoodsPrice.setTextColor(0xFF1933);
            binding.tvGoodsPriceSign.setTextColor(0xFF1933);
        }

        if (goods != null) {
            binding.tvGoodsName.setText(goods.getName());
            binding.tvGoodsPrice.setText(goods.getPrice());
            binding.tvGoodsNum.setText(mContext.getString(R.string.goods_num, goods.getNum()));
            ImageLoadHelper.loadImage(binding.ivGoodsCover, goods.getCover_pic(), (int) mContext.getResources().getDimension(R.dimen.dp_4), ConfigModel.getInstance().isGrayMode());
            ImageLoadHelper.loadImage(binding.ivGoodsQrcode, goods.getQrcode_url(), false);
        }
    }

    @Override
    protected void onActivityCreated() {
        super.onActivityCreated();
        Bundle args = getArguments();
        if (args != null) {
            grade = args.getInt("grade", 0);
        }
        if (getActivity() != null && grade == 0) {
            ViewModelProvider provider = new ViewModelProvider(getActivity());
            goodsViewModel = provider.get(GoodsViewModel.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getParentFragmentManager().clearFragmentResultListener("goods");
        getParentFragmentManager().clearFragmentResult("goods");
        if (goodsViewModel != null) {
            goodsViewModel.goodsLiveData.removeObservers(getViewLifecycleOwner());
        }
    }

    public void requestFocus() {

    }

}
