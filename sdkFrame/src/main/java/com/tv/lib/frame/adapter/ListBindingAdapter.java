package com.tv.lib.frame.adapter;

import android.view.View;
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

    protected OnItemClickListener<T> listener;

    public ListBindingAdapter(DiffUtil.ItemCallback<T> callback) {
        super(callback);
    }

    @NonNull
    @Override
    public final BindingViewHolder<VB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.d(TAG, "onCreateViewHolder");
        VB binding = createBinding(parent);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    onItemClick(v, (T) v.getTag());
                }
            }
        });
        binding.getRoot().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onItemFocusChanged(binding, hasFocus);
            }
        });
        return new BindingViewHolder<>(binding);
    }

    @Override
    public final void onBindViewHolder(@NonNull BindingViewHolder<VB> holder, int position) {
        holder.setPosition(position);
        T item = getItem(position);
        holder.getBinding().getRoot().setTag(item);
        onBindViewHolder(item, holder.getBinding(), position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }

    protected void onItemClick(View view, T item) {
        if (listener != null) {
            listener.onItemClick(item);
        }
    }

    protected void onItemFocusChanged(VB binding, boolean hasFocus) {

    }

    public abstract VB createBinding(ViewGroup parent);
    public abstract void onBindViewHolder(T data, VB binding, int position);

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }
}
