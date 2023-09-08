package cn.cibn.kaibo.ui.goods;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.GoodsListAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentGoodsListBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelGoods;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.player.VideoType;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.viewmodel.GoodsViewModel;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class GoodsListFragment extends KbBaseFragment<FragmentGoodsListBinding> {
    private static final String TAG = "GoodsListFragment";

    private GoodsViewModel goodsViewModel;
    private PlayerViewModel playerViewModel;
    private Observer<ModelLive.Item> playingVideoObserver;
    private int total;
    private GoodsListAdapter adapter;

    private boolean isGuideMode;
    private ModelLive.Item liveItem;

    private GoodsDetailFragment goodsDetailFragment;

    private boolean isOpened;

    public static GoodsListFragment createInstance() {
        Logger.d(TAG, "createInstance");
        return new GoodsListFragment();
    }

    @Override
    protected FragmentGoodsListBinding createBinding(LayoutInflater inflater) {
        return FragmentGoodsListBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        Logger.d(TAG, "initView");
        goodsDetailFragment = GoodsDetailFragment.createInstance(1);
        getChildFragmentManager().beginTransaction().replace(R.id.sub_goods_container, goodsDetailFragment).commit();

        binding.tvGoodsListTitle.setText(mContext.getResources().getString(R.string.goods_list_title, 0));
        adapter = new GoodsListAdapter();
        binding.recyclerGoods.setAdapter(adapter);
        binding.recyclerGoods.addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                int count = adapter.getItemCount();
                if (count - position < 4 && count < total && goodsViewModel != null) {
                    goodsViewModel.reqNextPage(liveItem.getId(), liveItem.getType());
                }
            }
        });
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelGoods.Item>() {
            @Override
            public void onItemClick(ModelGoods.Item item) {
                if (binding != null) {
                    Bundle data = new Bundle();
                    data.putSerializable("goodsDetail", item);
                    getChildFragmentManager().setFragmentResult("goods", data);
                    binding.goodsDrawer.openDrawer(GravityCompat.END);
                }
            }
        });
        if (goodsViewModel != null) {
            goodsViewModel.goodsLiveData.observe(getViewLifecycleOwner(), new Observer<ModelGoods>() {
                @Override
                public void onChanged(ModelGoods goodsModel) {
                    if (goodsModel.getType() == VideoType.LIVE.getValue()) {
                        updateGoodsList(goodsModel);
                    }
                }
            });
        }
        if (playerViewModel != null) {
            playingVideoObserver = new Observer<ModelLive.Item>() {
                @Override
                public void onChanged(ModelLive.Item item) {
                    binding.ivShopQrcode.setImageResource(R.drawable.default_qrcode);
                    updateLiveItem(item);
                }
            };
            playerViewModel.playingVideo.observe(getViewLifecycleOwner(), playingVideoObserver);
            updateLiveItem(playerViewModel.playingVideo.getValue());
        }
        updateView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_CURRENT_LIVE_UPDATE);
        keys.add(ChangedKeys.CHANGED_SELLING_INFO_UPDATE);
        keys.add(ChangedKeys.CHANGED_SHOP_QRCODE_UPDATE);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_SELLING_INFO_UPDATE.equals(key)) {
            adapter.notifyDataSetChanged();
        } else if (ChangedKeys.CHANGED_SHOP_QRCODE_UPDATE.equals(key)) {
            if (data instanceof String) {
                ImageLoadHelper.loadImage(binding.ivShopQrcode, (String) data, ConfigModel.getInstance().isGrayMode(), R.drawable.default_qrcode);
            } else {
                binding.ivShopQrcode.setImageResource(R.drawable.default_qrcode);
            }
        }
        super.onListenerChange(key, data);
    }

    @Override
    protected void updateView() {
        Logger.d(TAG, "updateView");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (binding != null) {
            binding.viewBgShopQrcode.setBackgroundResource(ConfigModel.getInstance().isGrayMode() ?
                    R.drawable.bg_shop_qrcode_gray : R.drawable.bg_shop_qrcode);
        }
    }

    @Override
    protected void onActivityCreated() {
        super.onActivityCreated();
        if (getActivity() != null) {
            ViewModelProvider provider = new ViewModelProvider(getActivity());
            goodsViewModel = provider.get(GoodsViewModel.class);
            playerViewModel = provider.get(PlayerViewModel.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (goodsViewModel != null) {
            goodsViewModel.goodsLiveData.removeObservers(getViewLifecycleOwner());
        }
        if (playerViewModel != null) {
            playerViewModel.playingVideo.removeObservers(getViewLifecycleOwner());
        }
    }

    public void requestFocus() {
        if (binding != null) {
            binding.recyclerGoods.post(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.requestFocus();
                    }
                }
            });
        }
    }

    public void setIsGuideMode(boolean isGuideMode) {
        if (this.isGuideMode == isGuideMode) {
            return;
        }
        this.isGuideMode = isGuideMode;
        if (isGuideMode) {
            binding.layoutPressBackToCloseGoodsList.getRoot().setVisibility(View.GONE);
        } else {
            binding.layoutPressBackToCloseGoodsList.getRoot().setVisibility(View.VISIBLE);
        }
    }

    private void updateLiveItem(ModelLive.Item liveItem) {
        if (liveItem == null) {
            return;
        }
        this.liveItem = liveItem;
    }

    private void updateGoodsList(ModelGoods goodsModel) {
        GoodsListFragment.this.total = goodsModel.getRow_count();
        binding.tvGoodsListTitle.setText(mContext.getResources().getString(R.string.goods_list_title, total));
        ImageLoadHelper.loadImage(binding.ivShopQrcode, goodsModel.getMch_qrcode(), false);

        adapter.submitList(goodsModel.getList());
        adapter.notifyDataSetChanged();
        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_GOODS_LIST_UPDATE);
        if (isOpened) {
            requestFocus();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.goodsDrawer.isDrawerOpen(GravityCompat.END)) {
                binding.goodsDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (binding.goodsDrawer.isDrawerOpen(GravityCompat.END)) {
                binding.goodsDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
        }
        return false;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }
}
