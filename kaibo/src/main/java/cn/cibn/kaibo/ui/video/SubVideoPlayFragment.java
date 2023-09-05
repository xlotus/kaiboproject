package cn.cibn.kaibo.ui.video;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.change.ChangeListenerManager;

import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentSubVideoPlayBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.goods.GoodsListFragment;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class SubVideoPlayFragment extends BaseStackFragment<FragmentSubVideoPlayBinding> {

    private VideoListFragment videoListFragment;
    private GoodsListFragment goodsListFragment;

    private VideoListDataSource videoListDataSource;

    private PlayerViewModel playerViewModel;

    public static SubVideoPlayFragment createInstance() {
        return new SubVideoPlayFragment();
    }

    @Override
    protected FragmentSubVideoPlayBinding createSubBinding(LayoutInflater inflater) {
        return FragmentSubVideoPlayBinding.inflate(inflater);
    }

    @Override
    protected void onActivityCreated() {
        super.onActivityCreated();
        if (getActivity() != null) {
            playerViewModel = new ViewModelProvider(getActivity()).get(PlayerViewModel.class);
            videoListDataSource = playerViewModel.videoListDataSource.getValue();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        videoListFragment = VideoListFragment.createInstance();
        goodsListFragment = GoodsListFragment.createInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(subBinding.goodsListContainer.getId(), goodsListFragment);
        transaction.replace(subBinding.videoListContainer.getId(), videoListFragment);
        transaction.commit();

        if (playerViewModel != null) {
            playerViewModel.playingVideo.observe(getViewLifecycleOwner(), new Observer<ModelLive.Item>() {
                @Override
                public void onChanged(ModelLive.Item item) {
                    videoListDataSource.setCurLiveId(item.getId());
                }
            });
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        super.requestFocus();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (!subBinding.drawerSubPlay.isDrawerOpen(GravityCompat.END)) {
                subBinding.drawerSubPlay.openDrawer(GravityCompat.END);
                goodsListFragment.requestFocus();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (subBinding.drawerSubPlay.isDrawerOpen(GravityCompat.END)) {
                if (goodsListFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                subBinding.drawerSubPlay.closeDrawer(GravityCompat.END);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!subBinding.drawerSubPlay.isDrawerOpen(GravityCompat.END) && videoListDataSource != null) {
                ModelLive.Item item = videoListDataSource.getNextLiveItem();
                if (item != null) {
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_SUB_PLAY, item);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!subBinding.drawerSubPlay.isDrawerOpen(GravityCompat.END) && videoListDataSource != null) {
                ModelLive.Item item = videoListDataSource.getPreLiveItem();
                if (item != null) {
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_SUB_PLAY, item);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (subBinding.drawerSubPlay.isDrawerOpen(GravityCompat.END)) {
                if (goodsListFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                subBinding.drawerSubPlay.closeDrawer(GravityCompat.END);
                return true;
            }
            ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_RESTORE_PLAY);
            goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (playerViewModel != null && playerViewModel.playingVideo.getValue() != null) {
                VideoOperateDialog.show(getParentFragmentManager(), playerViewModel.playingVideo.getValue());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (playerViewModel != null) {
            playerViewModel.playingVideo.removeObservers(getViewLifecycleOwner());
        }
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }
}
