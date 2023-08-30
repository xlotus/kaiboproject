package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.core.Logger;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import cn.cibn.kaibo.databinding.ItemSearchGuessBinding;

public class SearchGuessAdapter extends ListBindingAdapter<String, ItemSearchGuessBinding> {
    private static final String TAG = "VideoListAdapter";

    private View lastSelectedView = null;

    public SearchGuessAdapter() {
        super(new GuessDiffCallback());
    }

    @Override
    public ItemSearchGuessBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemSearchGuessBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(String item, ItemSearchGuessBinding binding, int position) {
        binding.tvSearchGuess.setText(item);
        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemSearchGuessBinding binding, boolean hasFocus) {
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

    private static class GuessDiffCallback extends DiffUtil.ItemCallback<String> {

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


