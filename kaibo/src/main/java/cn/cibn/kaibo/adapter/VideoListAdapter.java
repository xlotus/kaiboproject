package cn.cibn.kaibo.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
import cn.cibn.kaibo.data.CoverManager;
import cn.cibn.kaibo.databinding.ItemVideoBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelLive;

public class VideoListAdapter extends ListBindingAdapter<ModelLive.Item, ItemVideoBinding> {
    private static final String TAG = "VideoListAdapter";
    private Map<Boolean, ModelLive.Item> map = new HashMap<>();
    private View lastSelectedView = null;

    private String playingVideoId;

    public VideoListAdapter() {
        super(new LiveDiffCallback());
    }

    @Override
    public ItemVideoBinding createBinding(ViewGroup parent) {
        Logger.d(TAG, "createBinding");
        return ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(ModelLive.Item item, ItemVideoBinding binding, int position) {
        binding.bgFocusedLive.setBackgroundResource(ConfigModel.getInstance().isGrayMode() ?
                R.drawable.bg_recyclerview_item_gray : R.drawable.bg_recyclerview_item);

        String img = item.getBack_img();
        if (TextUtils.isEmpty(img)) {
            Bitmap bitmap = CoverManager.getInstance().getCover(item.getPlay_addr());
            if (bitmap != null) {
                binding.ivLiveCover.setImageBitmap(bitmap);
            }
        } else {
            ImageLoadHelper.loadImage(binding.ivLiveCover, img, (int) binding.getRoot().getResources().getDimension(R.dimen.dp_2), ConfigModel.getInstance().isGrayMode());
        }
//        ImageLoadHelper.loadCircleImage(binding.ivAnchorHead, item.getCover_img(), ConfigModel.getInstance().isGrayMode());
        binding.tvLiveName.setText(item.getTitle());
//        binding.tvAnchorName.setText(item.getName());
        setStyle(item, binding, binding.getRoot().hasFocus());
        if (position == 0) {
            lastSelectedView = binding.getRoot();
        }
    }

    @Override
    public void onItemFocusChanged(ItemVideoBinding binding, boolean hasFocus) {
        View itemView = binding.getRoot();
        ModelLive.Item item = (ModelLive.Item) itemView.getTag();
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

    private void setStyle(ModelLive.Item data, ItemVideoBinding binding, boolean hasFocus) {
        boolean living = Objects.equals(data.getId(), playingVideoId);
        if (hasFocus) {
            binding.bgFocusedLive.setSelected(true);
//            FocusUtils.scaleLeft(binding.ivLiveCover);
//            int offsetX = FocusUtils.scaleRight(binding.tvLiveName);
//            if (binding.ivLiveStatus.getVisibility() == View.VISIBLE) {
//                FocusUtils.scaleRight(binding.ivLiveStatus, offsetX);
//            }
//            offsetX = FocusUtils.scaleRight(binding.ivAnchorHead);
//            FocusUtils.scaleRight(binding.tvAnchorName, offsetX);

//            binding.ivLiveStatus.setVisibility(View.VISIBLE);
//            if (living) {
//                binding.ivLiveStatus.setVisibility(View.VISIBLE);
//                ImageLoadHelper.loadGif(binding.ivLiveStatus, R.drawable.ggshop_live_status_w, false);
//            } else {
//                binding.ivLiveStatus.setVisibility(View.GONE);
//            }

        } else {
            binding.bgFocusedLive.setSelected(false);
//            FocusUtils.resetScale(binding.ivLiveCover);
//            FocusUtils.resetScale(binding.tvLiveName);
//            FocusUtils.resetScale(binding.ivAnchorHead);
//            FocusUtils.resetScale(binding.tvAnchorName);
//            if (living) {
//                binding.ivLiveStatus.setVisibility(View.VISIBLE);
//                ImageLoadHelper.loadGif(binding.ivLiveStatus, R.drawable.ggshop_live_status, ConfigModel.getInstance().isGrayMode());
//            } else {
//                binding.ivLiveStatus.setVisibility(View.GONE);
//            }
        }
    }

    public void setLastSelectedView(View view) {
        lastSelectedView = view;
    }

    private static class LiveDiffCallback extends DiffUtil.ItemCallback<ModelLive.Item> {

        @Override
        public boolean areItemsTheSame(@NonNull ModelLive.Item oldItem, @NonNull ModelLive.Item newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ModelLive.Item oldItem, @NonNull ModelLive.Item newItem) {
            return oldItem.equals(newItem);
        }
    }
}


