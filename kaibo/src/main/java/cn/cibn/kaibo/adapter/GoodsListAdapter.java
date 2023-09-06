package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.ItemGoodsBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.utils.FocusUtils;

public class GoodsListAdapter extends ListBindingAdapter<ModelGoods.Item, ItemGoodsBinding> {

    private Map<Boolean, ModelGoods.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    private String sellingGoodsId;

    public GoodsListAdapter() {
        super(new LiveDiffCallback());
    }

    @Override
    public ItemGoodsBinding createBinding(ViewGroup parent) {
        return ItemGoodsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(ModelGoods.Item item, ItemGoodsBinding binding, int position) {
        View itemView = binding.getRoot();
        if (ConfigModel.getInstance().isGrayMode()) {
            binding.bgFocusedGoods.setBackgroundResource(R.drawable.bg_recyclerview_item_gray);
        } else {
            binding.bgFocusedGoods.setBackgroundResource(R.drawable.bg_recyclerview_item);
        }
        ImageLoadHelper.loadImage(binding.ivGoodsCover, item.getCover_pic(), (int) itemView.getResources().getDimension(R.dimen.dp_4), ConfigModel.getInstance().isGrayMode());
        binding.tvGoodsName.setText(item.getName());
        binding.tvGoodsPrice.setText(item.getPrice());
        binding.tvGoodsNum.setText(binding.tvGoodsNum.getResources().getString(R.string.goods_num, item.getNum()));
        binding.tvGoodsPrice.setTextColor(binding.tvGoodsPrice.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                R.color.white : R.color.color_ff1933));

        boolean isForSale = isForSale(item);
        setStyle(item, binding, itemView.hasFocus(), isForSale);

        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemGoodsBinding binding, boolean hasFocus) {
        View itemView = binding.getRoot();
        ModelGoods.Item item = (ModelGoods.Item) itemView.getTag();
        map.put(hasFocus, item);
        if (map.get(true) == map.get(false)) {
            // 获得焦点和失去焦点的是同一个item，会有以下两种情况：
            //  RecyclerView失去焦点
            //  RecyclerView重新获得焦点
            // 让此item保持选中状态，
            itemView.setSelected(true);
            lastSelectedView = itemView;
        } else {
            if (lastSelectedView != null) {
                lastSelectedView.setSelected(false);
            }
        }
        setStyle(item, binding, hasFocus, isForSale((ModelGoods.Item) itemView.getTag()));
    }

    public void requestFocus() {
        if (lastSelectedView != null) {
            lastSelectedView.requestFocus();
        }
    }

    private void setStyle(ModelGoods.Item data, ItemGoodsBinding binding, boolean hasFocus, boolean isForSale) {
        if (hasFocus) {
            binding.bgFocusedGoods.setSelected(true);
            FocusUtils.scaleLeft(binding.ivGoodsCover);
            FocusUtils.scaleRight(binding.tvGoodsName);
            int offsetX = FocusUtils.scaleRight(binding.tvGoodsPriceSign);
            FocusUtils.scaleRight(binding.tvGoodsPrice, offsetX);
            FocusUtils.scaleRight(binding.tvGoodsNum);
            binding.tvGoodsPriceSign.setTextColor(binding.tvGoodsPriceSign.getResources().getColor(R.color.white));
            binding.tvGoodsPrice.setTextColor(binding.tvGoodsName.getResources().getColor(R.color.white));
            binding.tvGoodsNum.setTextColor(binding.tvGoodsName.getResources().getColor(R.color.white));
            binding.tvGoodsName.setTextColor(binding.tvGoodsName.getResources().getColor(R.color.white));

            if (isForSale) {
                ImageLoadHelper.loadGif(binding.ivSaleStatus, R.drawable.ggshop_live_status_w, false);
                binding.ivSaleStatus.setVisibility(View.VISIBLE);
            } else {
                binding.ivSaleStatus.setVisibility(View.GONE);
            }

        } else {
            binding.bgFocusedGoods.setSelected(false);
            FocusUtils.resetScale(binding.ivGoodsCover);
            FocusUtils.resetScale(binding.tvGoodsName);
            FocusUtils.resetScale(binding.tvGoodsPrice);
            FocusUtils.resetScale(binding.tvGoodsNum);
//                tvGoodsPrice.setTextColor(tvGoodsName.getResources().getColor(R.color.color_ff1933));
            binding.tvGoodsPriceSign.setTextColor(binding.tvGoodsPriceSign.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                    R.color.white : R.color.color_ff1933));
            binding.tvGoodsPrice.setTextColor(binding.tvGoodsPrice.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                    R.color.white : R.color.color_ff1933));
            binding.tvGoodsNum.setTextColor(binding.tvGoodsName.getResources().getColor(R.color.color_cccccc));
            if (isForSale) {
//                    tvGoodsName.setTextColor(tvGoodsName.getResources().getColor(R.color.color_ff1535));
                binding.tvGoodsName.setTextColor(binding.tvGoodsPrice.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                        R.color.white : R.color.color_ff1535));
            } else {
                binding.tvGoodsName.setTextColor(binding.tvGoodsName.getResources().getColor(R.color.white));
            }
            if (isForSale) {
                ImageLoadHelper.loadGif(binding.ivSaleStatus, R.drawable.ggshop_live_status, ConfigModel.getInstance().isGrayMode());
                binding.ivSaleStatus.setVisibility(View.VISIBLE);
            } else {
                binding.ivSaleStatus.setVisibility(View.GONE);
            }
        }
    }

    private boolean isForSale(ModelGoods.Item item) {
        return Objects.equals(sellingGoodsId, item.getGoods_id());
    }

    private static class LiveDiffCallback extends DiffUtil.ItemCallback<ModelGoods.Item> {

        @Override
        public boolean areItemsTheSame(@NonNull ModelGoods.Item oldItem, @NonNull ModelGoods.Item newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ModelGoods.Item oldItem, @NonNull ModelGoods.Item newItem) {
            return oldItem.equals(newItem);
        }
    }
}


