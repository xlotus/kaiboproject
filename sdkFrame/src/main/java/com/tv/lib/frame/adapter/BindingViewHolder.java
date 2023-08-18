package com.tv.lib.frame.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

/**
 * @param <VB> 布局的ViewBinding
 */
public class BindingViewHolder<VB extends ViewBinding> extends RecyclerView.ViewHolder {
    private VB binding;
    protected int position;

    public BindingViewHolder(VB t) {
        super(t.getRoot());
        binding = t;
    }

    public VB getBinding() {
        return binding;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
