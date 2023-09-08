package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.core.Logger;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import cn.cibn.kaibo.databinding.ItemSearchHistoryBinding;
import cn.cibn.kaibo.db.SearchHistory;

public class SearchHistoryAdapter extends ListBindingAdapter<SearchHistory, ItemSearchHistoryBinding> {
    private static final String TAG = "VideoListAdapter";

    private View lastSelectedView = null;

    public SearchHistoryAdapter() {
        super(new HistoryDiffCallback());
    }

    @Override
    public ItemSearchHistoryBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(SearchHistory item, ItemSearchHistoryBinding binding, int position) {
        binding.tvSearchHistory.setText(item.getHistory());
        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemSearchHistoryBinding binding, boolean hasFocus) {
        if (hasFocus) {
            binding.getRoot().setSelected(true);
            lastSelectedView = binding.getRoot();
        } else {
            binding.getRoot().setSelected(false);
        }
    }

    public void requestFocus() {
        if (lastSelectedView != null) {
            lastSelectedView.requestFocus();
        }
    }

    private static class HistoryDiffCallback extends DiffUtil.ItemCallback<SearchHistory> {

        @Override
        public boolean areItemsTheSame(@NonNull SearchHistory oldItem, @NonNull SearchHistory newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull SearchHistory oldItem, @NonNull SearchHistory newItem) {
            return oldItem.equals(newItem);
        }
    }
}


