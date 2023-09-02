package cn.cibn.kaibo.utils;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import cn.cibn.kaibo.model.ModelAnchor;

public class AnchorDiffCallback extends DiffUtil.ItemCallback<ModelAnchor.Item> {

    @Override
    public boolean areItemsTheSame(@NonNull ModelAnchor.Item oldItem, @NonNull ModelAnchor.Item newItem) {
        return oldItem == newItem;
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull ModelAnchor.Item oldItem, @NonNull ModelAnchor.Item newItem) {
        return oldItem.equals(newItem);
    }
}
