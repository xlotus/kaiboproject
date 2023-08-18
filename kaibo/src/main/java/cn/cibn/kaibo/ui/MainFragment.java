package cn.cibn.kaibo.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.net.NetUtils;
import com.tv.lib.core.utils.ui.SafeToast;
import com.tv.lib.frame.activity.ILoading;

import java.util.ArrayList;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.RecommendModel;
import cn.cibn.kaibo.databinding.FragmentMainBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.settings.LiveSettings;
import cn.cibn.kaibo.stat.StatHelper;
import cn.cibn.kaibo.ui.goods.GoodsListFragment;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;
import cn.cibn.kaibo.ui.video.VideoPlayFragment;
import cn.cibn.kaibo.utils.ToastUtils;

public class MainFragment extends KbBaseFragment<FragmentMainBinding> implements ILoading {
    private static final String TAG = "MainFragment";
    private static final long MIN_BACK_DURATION = 3000L;

    private long lastBackTime;
    private float exitToastTranslationY;
    private ObjectAnimator exitToastShowAnimator, exitToastHideAnimator;

    private PlayerViewModel playerViewModel;
    private MenuFragment menuFragment;
    private VideoPlayFragment playerFragment;
    private GoodsListFragment goodsListFragment;


    private String intentLiveId;

    public static MainFragment createInstance(String intentLiveId) {
        Logger.d(TAG, "createInstance");
        Bundle args = new Bundle();
        args.putString("id", intentLiveId);
        MainFragment f = new MainFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated() {
        if (getActivity() != null) {
            playerViewModel = new ViewModelProvider(getActivity()).get(PlayerViewModel.class);
        }
    }

    @Override
    protected void updateView() {

    }

    @Override
    protected FragmentMainBinding createBinding(LayoutInflater inflater) {
        return FragmentMainBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        menuFragment = new MenuFragment();
        playerFragment = new VideoPlayFragment();
        goodsListFragment = GoodsListFragment.createInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.menu_container, menuFragment);
        transaction.replace(R.id.player_fragment_container, playerFragment);
        transaction.replace(R.id.navi_right_container, goodsListFragment);
        transaction.commit();
        exitToastTranslationY = mContext.getResources().getDimension(R.dimen.dp_140);
        binding.layoutPressBackToClose.setTranslationY(exitToastTranslationY);
        initAnimator();

        if (playerViewModel != null) {
            playerViewModel.isInPlay.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    updateView();
                }
            });
        }
    }

    @Override
    protected void initData() {
        binding.mainLiveDrawer.post(() -> {
            RecommendModel.getInstance().getNext();
        });
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {

            }

            @Override
            public void callback(Exception e) {
                binding.tvSdkLoading.setVisibility(View.GONE);
            }
        }, 0, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (playerViewModel != null) {
            playerViewModel.isInPlay.removeObservers(getViewLifecycleOwner());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_LIVE_LIST_UPDATE);
        keys.add(ChangedKeys.CHANGED_LIVE_ITEM_CLICKED);
        keys.add(ChangedKeys.CHANGED_GOODS_LIST_UPDATE);
        keys.add(ChangedKeys.CHANGED_NETWORK);
        keys.add(ChangedKeys.CHANGED_REQUEST_PLAY);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_LIVE_ITEM_CLICKED.equals(key)) {
            if (data instanceof ModelLive.Item) {
                ModelLive.Item item = (ModelLive.Item) data;
                StatHelper.statConfirmLiveRoom();
                reqUpdateLive(item);
                binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
            }
            return;
        }
        if (ChangedKeys.CHANGED_REQUEST_PLAY.equals(key)) {
            if (data instanceof ModelLive.Item) {
                ModelLive.Item item = (ModelLive.Item) data;
                RecommendModel.getInstance().addHistory(item);
                reqUpdateLive(item);
            }
            return;
        }
        if (ChangedKeys.CHANGED_LIVE_LIST_UPDATE.equals(key)) {
            hideLoading();
            return;
        }
        if (ChangedKeys.CHANGED_GOODS_LIST_UPDATE.equals(key)) {
            if (!LiveSettings.hasShownGuideEnter()) {
                LiveSettings.increaseShowGuideEnterCount();
                showGuide();
            }
            return;
        }
        if (ChangedKeys.CHANGED_NETWORK.equals(key)) {
            if (data instanceof Boolean && (Boolean) data) {
                binding.tvNoNetwork.setVisibility(View.GONE);
                if (!RecommendModel.getInstance().hasData()) {
                    RecommendModel.getInstance().getNext();
                }
//                if (liveWebSocket != null) {
//                    liveWebSocket.reconnect();
//                }
            }
            return;
        }
        super.onListenerChange(key, data);
    }

    @Override
    public void showLoading() {
        binding.tvSdkLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading(String text) {
        binding.tvSdkLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.tvSdkLoading.setVisibility(View.GONE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(TAG, "onKeyDown code = " + keyCode);
        if (isInPlay()) {
            return playerFragment.onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.END)) {
                if (goodsListFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                binding.mainLiveDrawer.closeDrawer(GravityCompat.END);
            } else {
                binding.mainLiveDrawer.openDrawer(GravityCompat.START);
                menuFragment.requestFocus();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
                if (menuFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
            } else {
                binding.mainLiveDrawer.openDrawer(GravityCompat.END);
                goodsListFragment.requestFocus();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (isDrawClosed()) {
                StatHelper.statSwitchUpLiveRoom();
                ModelLive.Item item = RecommendModel.getInstance().getPrev();
                if (item != null) {
                    reqUpdateLive(item);
                } else {
                    SafeToast.showToast("没有更多了", Toast.LENGTH_SHORT);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (isDrawClosed()) {
                StatHelper.statSwitchDownLiveRoom();
                ModelLive.Item item = RecommendModel.getInstance().getNext();
                if (item != null) {
                    reqUpdateLive(item);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
                if (menuFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
            if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.END)) {
                if (goodsListFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                binding.mainLiveDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
            if (lastBackTime == 0 || System.currentTimeMillis() - lastBackTime > MIN_BACK_DURATION) {
                lastBackTime = System.currentTimeMillis();
//                SafeToast.showToast("exit", Toast.LENGTH_SHORT);
                exitToastShowAnimator.start();

                binding.layoutPressBackToClose.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitToastHideAnimator.start();
                    }
                }, MIN_BACK_DURATION);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
//            if (playerViewModel != null) {
//                playerViewModel.setIsInPlay(true);
//            }
            return true;
        }
        return false;
    }

    private void initAnimator() {
        exitToastShowAnimator = ObjectAnimator.ofFloat(binding.layoutPressBackToClose, "translationY", exitToastTranslationY, 0);
        exitToastShowAnimator.setDuration(500);
        exitToastShowAnimator.setInterpolator(new OvershootInterpolator());
        exitToastHideAnimator = ObjectAnimator.ofFloat(binding.layoutPressBackToClose, "translationY", 0, exitToastTranslationY);
        exitToastHideAnimator.setDuration(500);
    }

    private boolean isInPlay() {
        return playerViewModel != null && playerViewModel.isInPlay();
    }

    private void reqUpdateLive(ModelLive.Item item) {
        if (playerFragment != null && item != null) {
            if (!NetUtils.isNetworkConnected(ObjectStore.getContext())) {
                ToastUtils.showToast(ObjectStore.getContext().getString(R.string.no_network));
            }
            playerFragment.updateLive(item);
//            if (liveWebSocket != null) {
//                liveWebSocket.reqChangeLive(item.getId());
//            }
        }
    }

    private void showGuide() {

    }

    private boolean isDrawClosed() {
        return !binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START) && !binding.mainLiveDrawer.isDrawerOpen(GravityCompat.END);
    }
}
