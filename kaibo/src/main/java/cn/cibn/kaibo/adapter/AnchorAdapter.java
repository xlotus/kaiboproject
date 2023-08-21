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
import java.util.Objects;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.ItemAnchorBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.utils.FocusUtils;

public class AnchorAdapter extends ListBindingAdapter<ModelAnchor.Item, ItemAnchorBinding> {
    private static final String TAG = "VideoListAdapter";
    private Map<Boolean, ModelAnchor.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    private String playingVideoId;

    public AnchorAdapter() {
        super(new LiveDiffCallback());
    }

    @Override
    public ItemAnchorBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemAnchorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(ModelAnchor.Item item, ItemAnchorBinding binding, int position) {
        binding.bgFocusedAnchor.setBackgroundResource(ConfigModel.getInstance().isGrayMode() ?
                R.drawable.bg_recyclerview_item_gray : R.drawable.bg_recyclerview_item);

        String img = item.getCover_img();
        ImageLoadHelper.loadImage(binding.ivAnchorCover, img, (int) binding.getRoot().getResources().getDimension(R.dimen.dp_2), ConfigModel.getInstance().isGrayMode());
        binding.tvLiveName.setText(item.getTitle());
        setStyle(item, binding, binding.getRoot().hasFocus());
        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemAnchorBinding binding, boolean hasFocus) {
        View itemView = binding.getRoot();
        ModelAnchor.Item item = (ModelAnchor.Item) itemView.getTag();
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

    private void setStyle(ModelAnchor.Item data, ItemAnchorBinding binding, boolean hasFocus) {
        boolean living = Objects.equals(data.getId(), playingVideoId);
        if (hasFocus) {
            binding.bgFocusedAnchor.setSelected(true);
            FocusUtils.scaleLeft(binding.ivAnchorCover);
            FocusUtils.scaleLeft(binding.tvLiveName);
        } else {
            binding.bgFocusedAnchor.setSelected(false);
            FocusUtils.resetScale(binding.ivAnchorCover);
            FocusUtils.resetScale(binding.tvLiveName);
        }
    }

    public void setLastSelectedView(View view) {
        lastSelectedView = view;
    }

    private static class LiveDiffCallback extends DiffUtil.ItemCallback<ModelAnchor.Item> {

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
}


