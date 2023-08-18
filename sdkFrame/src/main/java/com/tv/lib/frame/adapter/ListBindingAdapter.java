package com.tv.lib.frame.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.viewbinding.ViewBinding;

import com.tv.lib.core.Logger;

/**
 * 数据类型统一，单样式列表的适配器
 *
 * @param <T>  数据类型
 * @param <VB> 每行的ViewBinding
 */
public abstract class ListBindingAdapter<T, VB extends ViewBinding> extends ListAdapter<T, BindingViewHolder<VB>> {
    private static final String TAG = "ListBindingAdapter";

    public ListBindingAdapter(DiffUtil.ItemCallback<T> callback) {
        super(callback);
    }

    @NonNull
    @Override
    public final BindingViewHolder<VB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.d(TAG, "onCreateViewHolder");
        return new BindingViewHolder<>(createBinding(parent));
    }

    @Override
    public final void onBindViewHolder(@NonNull BindingViewHolder<VB> holder, int position) {
        holder.setPosition(position);
        onBindViewHolder(getItem(position), holder.getBinding(), position);
    }

    public abstract VB createBinding(ViewGroup parent);
    public abstract void onBindViewHolder(T data, VB binding, int position);
}
