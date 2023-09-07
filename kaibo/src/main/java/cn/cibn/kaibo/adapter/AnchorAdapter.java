package cn.cibn.kaibo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tv.lib.core.Logger;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.HashMap;
import java.util.Map;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.ItemAnchorBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.utils.AnchorDiffCallback;

public class AnchorAdapter extends ListBindingAdapter<ModelAnchor.Item, ItemAnchorBinding> {
    private static final String TAG = "VideoListAdapter";
    private Map<Boolean, ModelAnchor.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    private String playingVideoId;

    public AnchorAdapter() {
        super(new AnchorDiffCallback());
    }

    @Override
    public ItemAnchorBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemAnchorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(ModelAnchor.Item item, ItemAnchorBinding binding, int position) {
        binding.bgFocusedAnchor.setBackgroundResource(ConfigModel.getInstance().isGrayMode() ?
                R.drawable.bg_recyclerview_item_gray : R.drawable.bg_me_anchor_selector);

        String img = item.getCover_img();
        ImageLoadHelper.loadCircleImage(binding.ivAnchorCover, img, ConfigModel.getInstance().isGrayMode());
        binding.tvAnchorName.setText("@" + item.getName());
        binding.tvFansCount.setText("粉丝 " + item.getFans());
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
        if (hasFocus) {
            binding.bgFocusedAnchor.setSelected(true);
        } else {
            binding.bgFocusedAnchor.setSelected(false);
        }
    }

    public void setLastSelectedView(View view) {
        lastSelectedView = view;
    }

}


