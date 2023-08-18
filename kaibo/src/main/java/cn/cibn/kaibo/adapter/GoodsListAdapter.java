package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.utils.FocusUtils;

public class GoodsListAdapter extends ListAdapter<ModelGoods.Item, GoodsListAdapter.LiveViewHolder> {

    private Map<Boolean, ModelGoods.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    private String sellingGoodsId;

    public GoodsListAdapter() {
        super(new LiveDiffCallback());
    }

    @NonNull
    @Override
    public LiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveViewHolder holder, int position) {
        holder.bind(getItem(position));
        if (position == 0) {
            lastSelectedView = holder.itemView;
        }
    }

    public void requestFocus() {
        if (lastSelectedView != null) {
            lastSelectedView.requestFocus();
        }
    }

    public class LiveViewHolder extends RecyclerView.ViewHolder {
        private View bgFocused;
        private ImageView ivGoodsCover;
        private TextView tvGoodsName;
        private TextView tvGoodsPriceSign;
        private TextView tvGoodsPrice;
        private TextView tvGoodsNum;
        private ImageView ivSaleStatus;

        private ModelGoods.Item data;

        public LiveViewHolder(View v) {
            super(v);
            bgFocused = v.findViewById(R.id.bg_focused_goods);
            ivGoodsCover = v.findViewById(R.id.iv_goods_cover);
            tvGoodsName = v.findViewById(R.id.tv_goods_name);
            tvGoodsPriceSign = v.findViewById(R.id.tv_goods_price_sign);
            tvGoodsPrice = v.findViewById(R.id.tv_goods_price);
            tvGoodsNum = v.findViewById(R.id.tv_goods_num);
            ivSaleStatus = v.findViewById(R.id.iv_sale_status);

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    map.put(hasFocus, (ModelGoods.Item) v.getTag());
                    if (map.get(true) == map.get(false)) {
                        // 获得焦点和失去焦点的是同一个item，会有以下两种情况：
                        //  RecyclerView失去焦点
                        //  RecyclerView重新获得焦点
                        // 让此item保持选中状态，
                        v.setSelected(true);
                        lastSelectedView = v;
                    } else {
                        if (lastSelectedView != null) {
                            lastSelectedView.setSelected(false);
                        }
                    }
                    setStyle(hasFocus, isForSale((ModelGoods.Item) v.getTag()));
                }
            });
        }

        public void bind(ModelGoods.Item item) {
            itemView.setTag(item);
            data = item;
//            ivGoodsCover.setImageResource(R.drawable.cover);
            if (ConfigModel.getInstance().isGrayMode()) {
                bgFocused.setBackgroundResource(R.drawable.bg_recyclerview_item_gray);
            } else {
                bgFocused.setBackgroundResource(R.drawable.bg_recyclerview_item);
            }
            ImageLoadHelper.loadImage(ivGoodsCover, item.getCover_pic(), (int) itemView.getResources().getDimension(R.dimen.dp_2), ConfigModel.getInstance().isGrayMode());
            tvGoodsName.setText(item.getName());
            tvGoodsPrice.setText(item.getPrice());
            tvGoodsNum.setText(tvGoodsNum.getResources().getString(R.string.goods_num, item.getNum()));
            tvGoodsPrice.setTextColor(tvGoodsPrice.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                    R.color.white : R.color.color_ff1933));

            boolean isForSale = isForSale(item);
            setStyle(itemView.hasFocus(), isForSale);
        }

        private void setStyle(boolean hasFocus, boolean isForSale) {
            if (hasFocus) {
                bgFocused.setSelected(true);
                FocusUtils.scaleLeft(ivGoodsCover);
                FocusUtils.scaleRight(tvGoodsName);
                int offsetX = FocusUtils.scaleRight(tvGoodsPriceSign);
                FocusUtils.scaleRight(tvGoodsPrice, offsetX);
                FocusUtils.scaleRight(tvGoodsNum);
                tvGoodsPriceSign.setTextColor(tvGoodsPriceSign.getResources().getColor(R.color.white));
                tvGoodsPrice.setTextColor(tvGoodsName.getResources().getColor(R.color.white));
                tvGoodsNum.setTextColor(tvGoodsName.getResources().getColor(R.color.white));
                tvGoodsName.setTextColor(tvGoodsName.getResources().getColor(R.color.white));

                if (isForSale) {
                    ImageLoadHelper.loadGif(ivSaleStatus, R.drawable.ggshop_live_status_w, false);
                    ivSaleStatus.setVisibility(View.VISIBLE);
                } else {
                    ivSaleStatus.setVisibility(View.GONE);
                }

            } else {
                bgFocused.setSelected(false);
                FocusUtils.resetScale(ivGoodsCover);
                FocusUtils.resetScale(tvGoodsName);
                FocusUtils.resetScale(tvGoodsPrice);
                FocusUtils.resetScale(tvGoodsNum);
//                tvGoodsPrice.setTextColor(tvGoodsName.getResources().getColor(R.color.color_ff1933));
                tvGoodsPriceSign.setTextColor(tvGoodsPriceSign.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                        R.color.white : R.color.color_ff1933));
                tvGoodsPrice.setTextColor(tvGoodsPrice.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                        R.color.white : R.color.color_ff1933));
                tvGoodsNum.setTextColor(tvGoodsName.getResources().getColor(R.color.color_cccccc));
                if (isForSale) {
//                    tvGoodsName.setTextColor(tvGoodsName.getResources().getColor(R.color.color_ff1535));
                    tvGoodsName.setTextColor(tvGoodsPrice.getResources().getColor(ConfigModel.getInstance().isGrayMode() ?
                            R.color.white : R.color.color_ff1535));
                } else {
                    tvGoodsName.setTextColor(tvGoodsName.getResources().getColor(R.color.white));
                }
                if (isForSale) {
                    ImageLoadHelper.loadGif(ivSaleStatus, R.drawable.ggshop_live_status, ConfigModel.getInstance().isGrayMode());
                    ivSaleStatus.setVisibility(View.VISIBLE);
                } else {
                    ivSaleStatus.setVisibility(View.GONE);
                }
            }
        }

        private boolean isForSale(ModelGoods.Item item) {
            return Objects.equals(sellingGoodsId, item.getGoods_id());
        }
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


