package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.core.Logger;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.ItemMenuBinding;
import cn.cibn.kaibo.databinding.ItemSearchHistoryBinding;
import cn.cibn.kaibo.model.MenuItem;
import cn.cibn.kaibo.utils.StringDiffCallback;

public class MenuAdapter extends ListBindingAdapter<MenuItem, ItemMenuBinding> {
    private static final String TAG = "VideoListAdapter";

    private View lastSelectedView = null;

    public MenuAdapter() {
        super(new MenuDiffCallback());
    }

    @Override
    public ItemMenuBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(MenuItem item, ItemMenuBinding binding, int position) {
        binding.tvMenuName.setBackgroundResource(ConfigModel.getInstance().isGrayMode() ? R.drawable.bg_recyclerview_item_gray : R.drawable.bg_recyclerview_item);
        binding.tvMenuName.setText(item.getMenuName());
    }

    @Override
    protected void onItemClick(View view, MenuItem item) {
        super.onItemClick(view, item);
        lastSelectedView = view;
//        lastSelectedView.setSelected(true);
    }

    @Override
    public void onItemFocusChanged(ItemMenuBinding binding, boolean hasFocus) {
        if (hasFocus && lastSelectedView != null) {
            binding.getRoot().setSelected(false);
        }
    }

    public static class MenuDiffCallback extends DiffUtil.ItemCallback<MenuItem> {

        @Override
        public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}


