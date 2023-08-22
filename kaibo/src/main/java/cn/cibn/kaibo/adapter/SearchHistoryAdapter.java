package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.core.Logger;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.databinding.ItemSearchHistoryBinding;
import cn.cibn.kaibo.model.ModelAnchor;

public class SearchHistoryAdapter extends ListBindingAdapter<String, ItemSearchHistoryBinding> {
    private static final String TAG = "VideoListAdapter";

    public SearchHistoryAdapter() {
        super(new HistoryDiffCallback());
    }

    @Override
    public ItemSearchHistoryBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(String item, ItemSearchHistoryBinding binding, int position) {

        binding.tvSearchHistory.setText(item);
    }

    @Override
    public void onItemFocusChanged(ItemSearchHistoryBinding binding, boolean hasFocus) {

    }

    private static class HistoryDiffCallback extends DiffUtil.ItemCallback<String> {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }
}


