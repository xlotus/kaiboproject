package cn.cibn.kaibo.ui;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.net.NetUtils;
import com.tv.lib.core.utils.Utils;
import com.tv.lib.core.utils.ui.SafeToast;
import com.tv.lib.frame.activity.ILoading;

import java.util.ArrayList;
import java.util.Stack;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.RecommendModel;
import cn.cibn.kaibo.databinding.FragmentMainBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;
import cn.cibn.kaibo.player.VideoType;
import cn.cibn.kaibo.settings.LiveSettings;
import cn.cibn.kaibo.stat.StatHelper;
import cn.cibn.kaibo.ui.goods.GoodsDetailFragment;
import cn.cibn.kaibo.ui.goods.GoodsListFragment;
import cn.cibn.kaibo.ui.home.HomeFollowFragment;
import cn.cibn.kaibo.ui.me.MeFragment;
import cn.cibn.kaibo.ui.me.MeGroupFragment;
import cn.cibn.kaibo.ui.orders.OrdersHomeFragment;
import cn.cibn.kaibo.ui.search.SearchFragment;
import cn.cibn.kaibo.ui.settings.SettingsFragment;
import cn.cibn.kaibo.ui.video.SubVideoPlayFragment;
import cn.cibn.kaibo.ui.video.VideoOperateDialog;
import cn.cibn.kaibo.ui.video.VideoPlayFragment;
import cn.cibn.kaibo.ui.widget.ZanBean;
import cn.cibn.kaibo.utils.ParamsHelper;
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

    private VideoOperateDialog operateDialog;


    private String intentLiveId;

    private Stack<BaseStackFragment<?>> fragmentStack = new Stack<>();

    private boolean isSubPlaying; //播放的子页面的视频

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
        UserManager.getInstance().init();
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
        Logger.d(TAG, "channel = " + ParamsHelper.getChannel());
        Logger.d(TAG, "versionCode = " + Utils.getVersionCode(mContext));
        menuFragment = MenuFragment.createInstance();
        playerFragment = VideoPlayFragment.createInstance();
        goodsListFragment = GoodsListFragment.createInstance();
        goodsDetailFragment = GoodsDetailFragment.createInstance(0);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.menu_container, menuFragment);
        transaction.replace(R.id.player_fragment_container, playerFragment);
        transaction.replace(R.id.navi_right_container, goodsListFragment);
        transaction.commit();
        binding.mainLiveDrawer.setScrimColor(Color.TRANSPARENT);
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

        FragmentManager fragmentManager = getActivity() != null ? getActivity().getSupportFragmentManager() : getChildFragmentManager();
        fragmentManager.setFragmentResultListener("menu", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String page = result.getString("page");
                Logger.d(TAG, "requestKey = " + requestKey + ", page = " + page);
                if ("search".equals(page)) {
                    binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
                    SearchFragment f = new SearchFragment();
                    openStack(f);
                }
                else if ("me".equals(page)) {
                    MeFragment f = new MeFragment();
                    openStack(f);
                } else if ("homeFollow".equals(page)) {
                    HomeFollowFragment f = HomeFollowFragment.createInstance();
                    openStack(f);
                } else if ("meGroup".equals(page)) {
                    binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
                    fragmentStack.clear();
                    int subPage = result.getInt("subPage");
                    MeGroupFragment f = MeGroupFragment.createInstance(subPage);
                    openStack(f);
                }  else if ("back".equals(page)) {
                    backStack();
                } else if ("goHome".equals(page)) {
                    showHome();
                } else if ("subPlay".equals(page)) {
                    SubVideoPlayFragment f = SubVideoPlayFragment.createInstance();
                    openStack(f);
                } else if ("opFollow".equals(page)) {
//                    reqFollowAnchor();
                } else if ("opLike".equals(page)) {
//                    playLikeAnimation();
                } else if ("settings".equals(page)) {
                    binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
                    fragmentStack.clear();
                    int subPage = result.getInt("subPage");
                    SettingsFragment f = SettingsFragment.createInstance(subPage);
                    openStack(f);
                } else if ("orders".equals(page)) {
                    binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
                    fragmentStack.clear();
                    int subPage = result.getInt("subPage");
                    OrdersHomeFragment f = OrdersHomeFragment.createInstance(subPage);
                    openStack(f);
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
        Logger.d(TAG, "onDestroyView");
        if (playerViewModel != null) {
            playerViewModel.isInPlay.removeObservers(getViewLifecycleOwner());
        }
        if (operateDialog != null) {
            operateDialog.dismiss();
            operateDialog.setDismissListener(null);
            operateDialog.setOpListener(null);
            operateDialog = null;
        }
        stopLikeAnimation();
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
                isSubPlaying = true;
                ModelLive.Item item = (ModelLive.Item) data;
                RecommendModel.getInstance().addHistory(item, false);
                reqUpdateLive(item);
                binding.mainLiveDrawer.closeDrawer(GravityCompat.START);
            }
            return;
        }
        if (ChangedKeys.CHANGED_REQUEST_RESTORE_PLAY.equals(key)) {
            isSubPlaying = false;
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
                goodsListFragment.setOpened(false);
                binding.mainLiveDrawer.closeDrawer(GravityCompat.END);
            } else {
                if (!isSubPlaying && !binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
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
                        if (naviRightFragment == goodsListFragment){
                            goodsListFragment.setOpened(true);
                        }
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
                goodsListFragment.setOpened(false);
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
            if (playerViewModel != null && playerViewModel.playingVideo.getValue() != null) {
                operateDialog = VideoOperateDialog.show(getChildFragmentManager(), playerViewModel.playingVideo.getValue());
                operateDialog.setDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        operateDialog.setOpListener(null);
                        operateDialog.setDismissListener(null);
                        operateDialog = null;
                    }
                });
                operateDialog.setOpListener(new VideoOperateDialog.VideoOpListener() {
                    @Override
                    public void onFollowClick(ModelLive.Item item) {
                        if (item.getFollow() == 1) {
                            reqUnFollowAnchor(item);
                        }else {
                            reqFollowAnchor(item);
                        }
                    }

                    @Override
                    public void onGiveClick(ModelLive.Item item) {
                        if (item.getGive() == 1) {
                            reqCancelGiveVideo(item);
                        } else {
                            reqGiveVideo(item);
                        }
                    }
                });
            }
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

    private void openStack(BaseStackFragment<?> f) {
        fragmentStack.push(f);
        binding.stackContainer.setVisibility(View.VISIBLE);
        getChildFragmentManager().beginTransaction().replace(R.id.stack_container, f).commit();
        f.requestFocus();
    }

    //后退
    private boolean backStack() {
        if (!fragmentStack.isEmpty()) {
            fragmentStack.pop();
            if (fragmentStack.isEmpty()) {
                binding.stackContainer.setVisibility(View.GONE);
                if (binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
                    menuFragment.requestFocus();
                }
            } else {
                BaseStackFragment<?> prev = fragmentStack.peek();
                if (!prev.isFullScreen() && !binding.mainLiveDrawer.isDrawerOpen(GravityCompat.START)) {
                    binding.mainLiveDrawer.openDrawer(GravityCompat.START);
                }
                getChildFragmentManager().beginTransaction().replace(R.id.stack_container, prev).commit();
                prev.requestFocus();
            }
            return true;
        }
        return false;
    }

    private void showHome() {
        fragmentStack.clear();
        binding.stackContainer.setVisibility(View.GONE);
    }

    private void reqFollowAnchor(ModelLive.Item item) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<String> model;
            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().reqFollow(item.getAnchor_id());
            }

            @Override
            public void callback(Exception e) {
                if (model != null && (model.isSuccess() || model.getCode() == 1)) {
                    item.setFollow(1);
                    SafeToast.showToast("关注成功", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void reqUnFollowAnchor(ModelLive.Item item) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<String> model;
            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().reqUnFollow(item.getAnchor_id());
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess()) {
                    item.setFollow(0);
                    SafeToast.showToast("取消关注成功", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void reqGiveVideo(ModelLive.Item item) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<String> model;
            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().reqGive(item.getId(), item.getType());
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess()) {
                    item.setGive(1);
                    playLikeAnimation();
                    SafeToast.showToast("点赞成功", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void reqCancelGiveVideo(ModelLive.Item item) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<String> model;
            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().reqCancelGive(item.getId(), item.getType());
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess()) {
                    item.setGive(0);
                    SafeToast.showToast("取消点赞成功", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void playLikeAnimation() {
        binding.likeAnimatorView.post(likeAnimationTask);
        likeAnimationTask.count = 0;
    }

    private void stopLikeAnimation() {
        if (binding != null) {
            binding.likeAnimatorView.removeCallbacks(likeAnimationTask);
        }
    }

    private LikeAnimationTask likeAnimationTask = new LikeAnimationTask();
    private class LikeAnimationTask extends Thread {
        public int count = 0;
        @Override
        public void run() {
            ++count;
            ZanBean bean = new ZanBean(mContext, R.drawable.like_heart, binding.likeAnimatorView);
            binding.likeAnimatorView.addZanXin(bean);
            if (count < 20) {
                binding.likeAnimatorView.postDelayed(this, 200);
            }
        }
    };
}
