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

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.ItemVersionHistoryBinding;
import cn.cibn.kaibo.model.ModelVersion;
import cn.cibn.kaibo.utils.DateUtils;

public class VersionHistoryAdapter extends ListBindingAdapter<ModelVersion.Item, ItemVersionHistoryBinding> {
    private static final String TAG = "VideoListAdapter";
    private Map<Boolean, ModelVersion.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    public VersionHistoryAdapter() {
        super(new VersionDiffCallback());
    }

    @Override
    public ItemVersionHistoryBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemVersionHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(ModelVersion.Item item, ItemVersionHistoryBinding binding, int position) {
        binding.tvVersionName.setText("版本：" + item.getVersionNumber());
        binding.tvFileSize.setText("大小：" + item.getFileSize());
        binding.tvVersionTime.setText("更新时间：" + DateUtils.formatDate(item.getUpdateTime() * 1000));
        setStyle(item, binding, binding.getRoot().hasFocus());
        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemVersionHistoryBinding binding, boolean hasFocus) {
        View itemView = binding.getRoot();
        ModelVersion.Item item = (ModelVersion.Item) itemView.getTag();
        map.put(hasFocus, item);
        if (map.get(true) == map.get(false)) {
            // 获得焦点和失去焦点的是同一个item，会有以下两种情况：
            //  RecyclerView失去焦点
            //  RecyclerView重新获得焦点
            // 让此item保持选中状态，
            itemView.setSelected(true);
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

    private void setStyle(ModelVersion.Item data, ItemVersionHistoryBinding binding, boolean hasFocus) {
        if (hasFocus) {
            binding.bgVersionHistoryFocused.setBackgroundResource(ConfigModel.getInstance().isGrayMode() ? R.drawable.bg_recyclerview_focus_gray : R.drawable.bg_recyclerview_focus);
            binding.bgVersionHistoryFocused.setVisibility(View.VISIBLE);
            binding.bgVersionHistoryN.setVisibility(View.GONE);
        } else {
            binding.bgVersionHistoryFocused.setVisibility(View.GONE);
            binding.bgVersionHistoryN.setVisibility(View.VISIBLE);
        }
    }

    public void setLastSelectedView(View view) {
        lastSelectedView = view;
    }

    private static class VersionDiffCallback extends DiffUtil.ItemCallback<ModelVersion.Item> {

        @Override
        public boolean areItemsTheSame(@NonNull ModelVersion.Item oldItem, @NonNull ModelVersion.Item newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ModelVersion.Item oldItem, @NonNull ModelVersion.Item newItem) {
            return oldItem.equals(newItem);
        }
    }
}


