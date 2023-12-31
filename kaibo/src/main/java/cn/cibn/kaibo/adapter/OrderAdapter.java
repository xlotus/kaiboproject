package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.ItemOrderBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelOrder;
import cn.cibn.kaibo.model.OrderAction;

public class OrderAdapter extends ListBindingAdapter<ModelOrder.Item, ItemOrderBinding> implements View.OnClickListener {

    private Map<Boolean, ModelOrder.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    private OrderActionListener orderActionListener;

    private int orderStatus;

    public OrderAdapter() {
        super(new OrderDiffCallback());
    }

    @Override
    public ItemOrderBinding createBinding(ViewGroup parent) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.btnOrderRight.setOnClickListener(this);
        binding.btnOrderLeft.setOnClickListener(this);
        return binding;
    }

    @Override
    public void onBindViewHolder(ModelOrder.Item item, ItemOrderBinding binding, int position) {
        View itemView = binding.getRoot();
        if (ConfigModel.getInstance().isGrayMode()) {
            binding.btnOrderLeft.setBackgroundResource(R.drawable.bg_order_item_btn_selector_gray);
            binding.btnOrderRight.setBackgroundResource(R.drawable.bg_order_item_btn_selector_gray);
            binding.btnOrderLeft.setTextColor(Color.WHITE);
            binding.btnOrderRight.setTextColor(Color.WHITE);
            binding.tvOrderStatus.setTextColor(Color.WHITE);
        } else {
            binding.btnOrderLeft.setBackgroundResource(R.drawable.bg_order_item_btn_selector);
            binding.btnOrderRight.setBackgroundResource(R.drawable.bg_order_item_btn_selector);
            binding.btnOrderLeft.setTextColor(itemView.getResources().getColorStateList(R.color.order_btn_selector));
            binding.btnOrderRight.setTextColor(itemView.getResources().getColorStateList(R.color.order_btn_selector));
            binding.tvOrderStatus.setTextColor(0xFFFF3C5B);
        }
        if (item.getGoodsList() != null && !item.getGoodsList().isEmpty()) {
            ModelOrder.GoodsListItem goods = item.getGoodsList().get(0);
            ImageLoadHelper.loadImage(binding.ivGoodsCover, goods.getGoodsPic(), (int) itemView.getResources().getDimension(R.dimen.dp_2), ConfigModel.getInstance().isGrayMode());
            binding.tvGoodsName.setText(goods.getGoodsName());
            binding.tvGoodsPrice.setText(goods.getPrice());
            binding.tvGoodsNum.setText("x" + goods.getNum());
        }
        binding.tvOrderCost.setText("¥ " + item.getPayPrice());

        binding.btnOrderLeft.setVisibility(View.GONE);
        binding.btnOrderRight.setVisibility(View.GONE);
        binding.tvOrderStatus.setVisibility(View.GONE);
        if (orderStatus == 0) {
            binding.btnOrderRight.setVisibility(View.VISIBLE);
            binding.btnOrderRight.setText(OrderAction.PAY.getActionName());
            binding.btnOrderRight.setTag(R.id.tag_order_item, item);
            binding.btnOrderRight.setTag(R.id.tag_order_action, OrderAction.PAY);
        } else if (orderStatus == 1) {
            binding.tvOrderStatus.setVisibility(View.VISIBLE);
            binding.tvOrderStatus.setText("待发货");
        } else if (orderStatus == 2) {
            binding.btnOrderRight.setVisibility(View.VISIBLE);
            binding.btnOrderRight.setText(OrderAction.CONFIRM_RECEIPT.getActionName());
            binding.btnOrderRight.setTag(R.id.tag_order_item, item);
            binding.btnOrderRight.setTag(R.id.tag_order_action, OrderAction.CONFIRM_RECEIPT);
            binding.btnOrderLeft.setVisibility(View.VISIBLE);
            binding.btnOrderLeft.setText(OrderAction.VIEW_LOGISTICS.getActionName());
            binding.btnOrderLeft.setTag(R.id.tag_order_item, item);
            binding.btnOrderLeft.setTag(R.id.tag_order_action, OrderAction.VIEW_LOGISTICS);
        } else if (orderStatus == 3) {
            binding.tvOrderStatus.setVisibility(View.VISIBLE);
            binding.tvOrderStatus.setText("已完成");
        }
        else if (orderStatus == 4) {
            if (item.getRefundStatus() == 0) {
                binding.tvOrderStatus.setVisibility(View.VISIBLE);
                binding.tvOrderStatus.setText("待处理");
            } else if (item.getRefundStatus() == 1) {
                binding.tvOrderStatus.setVisibility(View.VISIBLE);
                binding.tvOrderStatus.setText("已完成");
            } else if (item.getRefundStatus() == 3) {
                binding.tvOrderStatus.setVisibility(View.VISIBLE);
                binding.tvOrderStatus.setText("已拒绝");
            } else if (item.getIsAgree() == 1) {
                binding.btnOrderRight.setVisibility(View.VISIBLE);
                binding.btnOrderRight.setText(OrderAction.RETURN.getActionName());
                binding.btnOrderRight.setTag(R.id.tag_order_item, item);
                binding.btnOrderRight.setTag(R.id.tag_order_action, OrderAction.RETURN);
            }
        }

        setStyle(item, binding, itemView.hasFocus());

        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemOrderBinding binding, boolean hasFocus) {
        View itemView = binding.getRoot();
        ModelOrder.Item item = (ModelOrder.Item) itemView.getTag();
        map.put(hasFocus, item);
        if (map.get(true) == map.get(false)) {
            // 获得焦点和失去焦点的是同一个item，会有以下两种情况：
            //  RecyclerView失去焦点
            //  RecyclerView重新获得焦点
            // 让此item保持选中状态，
//            itemView.setSelected(true);
            lastSelectedView = itemView;
        } else {
            if (lastSelectedView != null) {
                lastSelectedView.setSelected(false);
            }
        }
        setStyle(item, binding, hasFocus);
    }

    public void requestFocus() {
        if (lastSelectedView != null) {
            lastSelectedView.requestFocus();
        }
    }

    private void setStyle(ModelOrder.Item data, ItemOrderBinding binding, boolean hasFocus) {

    }

    @Override
    public void onClick(View v) {
        if (orderActionListener == null) {
            return;
        }
        Object data = v.getTag(R.id.tag_order_item);
        Object action = v.getTag(R.id.tag_order_action);
        if (data instanceof ModelOrder.Item && action instanceof OrderAction) {
            orderActionListener.onOrderAction((ModelOrder.Item) data, (OrderAction) action);
        }
    }

    public void setOrderActionListener(OrderActionListener orderActionListener) {
        this.orderActionListener = orderActionListener;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    private static class OrderDiffCallback extends DiffUtil.ItemCallback<ModelOrder.Item> {

        @Override
        public boolean areItemsTheSame(@NonNull ModelOrder.Item oldItem, @NonNull ModelOrder.Item newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ModelOrder.Item oldItem, @NonNull ModelOrder.Item newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OrderActionListener {
        void onOrderAction(ModelOrder.Item item, OrderAction action);
    }
}


