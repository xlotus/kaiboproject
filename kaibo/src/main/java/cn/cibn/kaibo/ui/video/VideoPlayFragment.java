package cn.cibn.kaibo.ui.video;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;

import java.util.ArrayList;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentVideoPlayBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.player.PlayerWrapper;
import cn.cibn.kaibo.stat.StatHelper;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.viewmodel.GoodsViewModel;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class VideoPlayFragment extends KbBaseFragment<FragmentVideoPlayBinding> {
    private static final String TAG = "PlayerFragment";

    private ModelLive.Item liveItem;

    private ObjectAnimator loadingCoverShowAnimator, loadingCoverHideAnimator;

    private long startLiveTime;
    private PlayerViewModel playerViewModel;
    private GoodsViewModel goodsViewModel;

    private PlayerWrapper player;
    private TXCloudVideoView videoView;

    public static VideoPlayFragment createInstance() {
        return new VideoPlayFragment();
    }

    @Override
    protected FragmentVideoPlayBinding createBinding(LayoutInflater inflater) {
        return FragmentVideoPlayBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        if (playerViewModel != null) {
            playerViewModel.isInPlay.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    updateView();
                }
            });
        }
        binding.getRoot().post(initPlayerTask);
        initAnimator();
        updateView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {
        if (binding == null) {
            return;
        }
        Logger.d(TAG, "updateView start");
        ImageLoadHelper.loadResource(binding.ivQrCodeBg, ConfigModel.getInstance().isGrayMode() ? R.drawable.bg_qrcode_live : R.drawable.bg_qrcode_live);
        ImageLoadHelper.loadResource(binding.ivLiveLoadingCover, ConfigModel.getInstance().isGrayMode() ? R.drawable.ggshop_live_loading_gray : R.drawable.ggshop_live_loading);
        if (liveItem == null) {
            binding.layoutLiveAnchorInfo.setVisibility(View.GONE);
        } else {
            binding.layoutLiveAnchorInfo.setVisibility(View.VISIBLE);
            ImageLoadHelper.loadCircleImage(binding.ivLiveAnchorHead, liveItem.getCover_img(), ConfigModel.getInstance().isGrayMode());
            binding.tvLiveAnchorName.setText("@" + liveItem.getTitle());
            String no = liveItem.getCertification_no() == null ? "" : liveItem.getCertification_no();
            binding.tvLiveAnchorCertificationNo.setText(no);

//            ImageLoadHelper.loadResource(binding.ivLiveAnchorV, ConfigModel.getInstance().isGrayMode() ? R.drawable.ggshop_icon_v_gray : R.drawable.ggshop_icon_v);
        }
        Logger.d(TAG, "updateView end");
    }

    @Override
    public void onActivityCreated() {
        if (getActivity() != null) {
            ViewModelProvider provider = new ViewModelProvider(getActivity());
            playerViewModel = provider.get(PlayerViewModel.class);
            goodsViewModel = provider.get(GoodsViewModel.class);
        }
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
        if (binding != null) {
            binding.getRoot().removeCallbacks(initPlayerTask);
        }
        if (player != null) {
            if (player.isPlaying()) {
                player.stopPlay();
            }
            if (videoView != null) {
                videoView.onDestroy();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_SELLING_INFO_UPDATE);
        keys.add(ChangedKeys.CHANGED_LIVE_ON);
        keys.add(ChangedKeys.CHANGED_LIVE_OFF);
        keys.add(ChangedKeys.CHANGED_NETWORK);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_LIVE_LIST_UPDATE.equals(key)) {
            Logger.d(TAG, "onListenerChange, CHANGED_LIVE_LIST_UPDATE");

            return;
        }
        super.onListenerChange(key, data);
    }

    private void initAnimator() {
        loadingCoverShowAnimator = ObjectAnimator.ofFloat(binding.ivLiveLoadingCover, "alpha", 0.0f, 1.0f);
        loadingCoverShowAnimator.setDuration(300);
        loadingCoverHideAnimator = ObjectAnimator.ofFloat(binding.ivLiveLoadingCover, "alpha", 1.0f, 0.0f);
        loadingCoverHideAnimator.setDuration(200);
    }

    private void initPlayer() {
        videoView = new TXCloudVideoView(mContext);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        binding.getRoot().addView(videoView, 0, flp);
        player = new PlayerWrapper(mContext);
        player.setPlayerView(videoView);
        player.setVodChangeListener(new PlayerWrapper.OnPlayEventChangedListener() {
            @Override
            public void onProgress(Bundle bundle) {

            }

            @Override
            public void onRcvFirstFrame() {
                hideLoadingCover();
            }
        });
        if (liveItem != null) {
            play();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(TAG, "onKeyDown code = " + keyCode);

        return false;
    }

    private boolean isInPlay() {
        return playerViewModel != null && playerViewModel.isInPlay();
    }


    public void updateLive(ModelLive.Item item) {
        Logger.d(TAG, "updateLive");
        if (liveItem == item) {
            return;
        }
        if (liveItem != null) {
            StatHelper.statExitLiveRoom(liveItem);
        }
        StatHelper.statEnterLiveRoom(item);
        Logger.d(TAG, "updateLive item = " + item);
        liveItem = item;
        if (playerViewModel != null) {
            playerViewModel.playingVideo.setValue(liveItem);
        }
        ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_CURRENT_LIVE_UPDATE);
//        SafeToast.showToast("start live " + item.getName(), Toast.LENGTH_SHORT);
        binding.ivQrCode.setImageResource(R.drawable.default_qrcode);
        reqQrCode();
        if (goodsViewModel != null) {
            goodsViewModel.reqFirstPage(liveItem.getId(), liveItem.getType());
        }

        updateView();

        showLoadingCover();
        play();
//        if (player != null) {
//            player.setOnErrorListener(null);
//            player.setOnVideoStateChangeListener(null);
//            if (player.isPlaying()) {
//                player.stopPlayback();
//            }
//            player.release();
//            player = null;
//        }
//        rootLayout.post(initPlayerTask);
    }

    private void play() {
        startLiveTime = System.currentTimeMillis();
        if (player == null || liveItem == null) {
            return;
        }
        Logger.d(TAG, "start play " + liveItem.getPlay_addr());
//        String url = "https://ggplay.oeob.net/live/5d9d4d66e09d270689a0bc381350ac6a.flv?txSecret=01e70dd4eff4acf3bb5de1c679dfcfae&txTime=643F4F55";
        player.preStartPlay(liveItem.getPlay_addr());
    }

    private void showLoadingCover() {
//        ivLoadingCover.setVisibility(View.VISIBLE);
        if (binding.ivLiveLoadingCover.getAlpha() > 0.99) {
            return;
        }
        if (loadingCoverShowAnimator != null) {
            loadingCoverShowAnimator.start();
        }
    }

    private void hideLoadingCover() {
//        ivLoadingCover.setVisibility(View.GONE);
        if (binding.ivLiveLoadingCover.getAlpha() < 0.01) {
            return;
        }
        if (loadingCoverHideAnimator != null) {
            loadingCoverHideAnimator.start();
        }
    }

    private void reqQrCode() {

    }

    private Runnable initPlayerTask = this::initPlayer;
}
