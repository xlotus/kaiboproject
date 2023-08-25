package cn.cibn.kaibo.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentResultListener;
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
import java.util.Stack;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.RecommendModel;
import cn.cibn.kaibo.databinding.FragmentMainBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.player.VideoType;
import cn.cibn.kaibo.settings.LiveSettings;
import cn.cibn.kaibo.stat.StatHelper;
import cn.cibn.kaibo.ui.goods.GoodsDetailFragment;
import cn.cibn.kaibo.ui.goods.GoodsListFragment;
import cn.cibn.kaibo.ui.me.AnchorFragment;
import cn.cibn.kaibo.ui.me.FollowFragment;
import cn.cibn.kaibo.ui.me.MeFragment;
import cn.cibn.kaibo.ui.search.SearchFragment;
import cn.cibn.kaibo.ui.video.VideoPlayFragment;
import cn.cibn.kaibo.utils.ToastUtils;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

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
    private GoodsDetailFragment goodsDetailFragment;
    private KbBaseFragment<?> naviRightFragment;


    private String intentLiveId;

    private Stack<KbBaseFragment> fragmentStack = new Stack<>();

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
        menuFragment = MenuFragment.createInstance();
        playerFragment = VideoPlayFragment.createInstance();
        goodsListFragment = GoodsListFragment.createInstance();
        goodsDetailFragment = GoodsDetailFragment.createInstance(0);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.menu_container, menuFragment);
        transaction.replace(R.id.player_fragment_container, playerFragment);
        transaction.replace(R.id.navi_right_container, goodsListFragment);
        transaction.commit();
        naviRightFragment = goodsListFragment;
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

        getChildFragmentManager().setFragmentResultListener("menu", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String page = result.getString("page");
                Logger.d(TAG, "requestKey = " + requestKey + ", page = " + page);
                if ("search".equals(page)) {
                    binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
                    SearchFragment f = new SearchFragment();
                    fragmentStack.push(f);
                    binding.stackContainer.setVisibility(View.VISIBLE);
                    getChildFragmentManager().beginTransaction().replace(R.id.stack_container, f).commit();
                    f.requestFocus();
                }
                else if ("me".equals(page)) {
                    MeFragment f = new MeFragment();
                    fragmentStack.push(f);
                    binding.stackContainer.setVisibility(View.VISIBLE);
                    getChildFragmentManager().beginTransaction().replace(R.id.stack_container, f).commit();
                    f.requestFocus();
                } else if ("follow".equals(page)) {
                    FollowFragment f = new FollowFragment();
                    fragmentStack.push(f);
                    binding.stackContainer.setVisibility(View.VISIBLE);
                    getChildFragmentManager().beginTransaction().replace(R.id.stack_container, f).commit();
                    f.requestFocus();
                } else if ("anchor".equals(page)) {
                    AnchorFragment f = new AnchorFragment();
                    fragmentStack.push(f);
                    binding.stackContainer.setVisibility(View.VISIBLE);
                    getChildFragmentManager().beginTransaction().replace(R.id.stack_container, f).commit();
                    f.requestFocus();
                } else if ("back".equals(page)) {
                    backStack();
                }
            }
        });
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
        getChildFragmentManager().clearFragmentResultListener("menu");
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
        keys.add(ChangedKeys.CHANGED_REQUEST_SUB_PLAY);
        keys.add(ChangedKeys.CHANGED_REQUEST_RESTORE_PLAY);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_LIVE_ITEM_CLICKED.equals(key)) {
            if (data instanceof ModelLive.Item) {
                ModelLive.Item item = (ModelLive.Item) data;
                StatHelper.statConfirmLiveRoom();
                RecommendModel.getInstance().addHistory(item, true);
                reqUpdateLive(item);
                binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
            }
            return;
        }
        if (ChangedKeys.CHANGED_REQUEST_PLAY.equals(key)) {
            if (data instanceof ModelLive.Item) {
                ModelLive.Item item = (ModelLive.Item) data;
                RecommendModel.getInstance().addHistory(item, true);
                reqUpdateLive(item);
            }
            return;
        }

        if (ChangedKeys.CHANGED_REQUEST_SUB_PLAY.equals(key)) {
            if (data instanceof ModelLive.Item) {
                ModelLive.Item item = (ModelLive.Item) data;
                RecommendModel.getInstance().addHistory(item, false);
                reqUpdateLive(item);
            }
            return;
        }
        if (ChangedKeys.CHANGED_REQUEST_RESTORE_PLAY.equals(key)) {
            ModelLive.Item item = RecommendModel.getInstance().getCurrent();
            reqUpdateLive(item);
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
        if (!fragmentStack.isEmpty()) {
            return fragmentStack.peek().onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.END)) {
                if (naviRightFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                binding.mainLiveDrawer.closeDrawer(GravityCompat.END);
            } else {
                if (!binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
                    binding.mainLiveDrawer.openDrawer(GravityCompat.START);
                    menuFragment.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
                if (menuFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
                binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
            } else {
                if (!binding.mainLiveDrawer.isDrawerOpen(GravityCompat.END)) {
                    binding.naviRightContainer.post(() -> {
                        naviRightFragment.requestFocus();
                        binding.mainLiveDrawer.openDrawer(GravityCompat.END);
                    });
                }
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
                if (naviRightFragment.onKeyDown(keyCode, event)) {
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
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (item.getType() == VideoType.LIVE.getValue()) {
                transaction.replace(R.id.navi_right_container, goodsListFragment);
                naviRightFragment = goodsListFragment;
            } else {
                transaction.replace(R.id.navi_right_container, goodsDetailFragment);
                naviRightFragment = goodsDetailFragment;
            }
            transaction.commit();
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

    //后退
    private boolean backStack() {
        if (!fragmentStack.isEmpty()) {
            fragmentStack.pop();
            if (fragmentStack.isEmpty()) {
                binding.stackContainer.setVisibility(View.GONE);
                menuFragment.requestFocus();
            } else {
                KbBaseFragment<?> prev = fragmentStack.peek();
                getChildFragmentManager().beginTransaction().replace(R.id.stack_container, prev).commit();
                prev.requestFocus();
            }
            return true;
        }
        return false;
    }
}
