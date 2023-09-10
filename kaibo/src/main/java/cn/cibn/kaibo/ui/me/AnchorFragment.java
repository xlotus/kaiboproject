package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.ui.SafeToast;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.VideoListAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentAnchorBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;
import cn.cibn.kaibo.network.UserMethod;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.ui.video.VideoListDataSource;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class AnchorFragment extends KbBaseFragment<FragmentAnchorBinding> implements View.OnClickListener {

    private VideoListAdapter adapter;
    private AnchorVideoDataSource videoSource;

    private PlayerViewModel playerViewModel;

    private ModelAnchor.Item anchor;

    private boolean isOpen = false;

    public static AnchorFragment createInstance() {
        return new AnchorFragment();
    }

    @Override
    protected FragmentAnchorBinding createBinding(LayoutInflater inflater) {
        return FragmentAnchorBinding.inflate(inflater);
    }

    @Override
    protected void onActivityCreated() {
        super.onActivityCreated();
        if (getActivity() != null) {
            playerViewModel = new ViewModelProvider(getActivity()).get(PlayerViewModel.class);
        }
    }

    @Override
    protected void initView() {
        binding.tvAnchorVideoListTitle.setText(getString(R.string.anchor_video_list_title, 0));
        binding.btnFollow.setOnClickListener(this);
        adapter = new VideoListAdapter();
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelLive.Item>() {
            @Override
            public void onItemClick(ModelLive.Item item) {
                if (getActivity() != null) {
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_SUB_PLAY, item);
                    Bundle result = new Bundle();
                    result.putString("page", "subPlay");
                    getActivity().getSupportFragmentManager().setFragmentResult("menu", result);
                }
            }
        });
        binding.recyclerAnchorVideoList.setAdapter(adapter);

        if (playerViewModel != null) {
            playerViewModel.playingVideo.observe(getViewLifecycleOwner(), new Observer<ModelLive.Item>() {
                @Override
                public void onChanged(ModelLive.Item item) {
                    videoSource.setCurLiveId(item.getId());
                }
            });
        }
    }

    @Override
    protected void initData() {
        videoSource = new AnchorVideoDataSource();
        videoSource.setOnReadyListener(new VideoListDataSource.OnReadyListener() {
            @Override
            public void onSourceReady() {
                if (videoSource != null && binding != null) {
                    binding.tvAnchorVideoListTitle.setText(getString(R.string.anchor_video_list_title, videoSource.getTotal()));
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                if (isOpen) {
                    requestFocus();
                }
            }
        });
        if (playerViewModel != null) {
            playerViewModel.videoListDataSource.setValue(videoSource);
        }
        adapter.submitList(videoSource.getLiveList());
    }

    @Override
    protected void updateView() {
        if (this.anchor != null) {
            binding.tvAnchorName.setText("@" + anchor.getName());
            binding.tvAnchorId.setText("用户ID:" + anchor.getAnchor_id());
            binding.btnFollow.setText(anchor.getFollow() == 1? R.string.cancel_follow : R.string.follow);
            ImageLoadHelper.loadCircleImage(binding.ivUserHead, anchor.getCover_img(), ConfigModel.getInstance().isGrayMode());
        }
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        if (binding != null) {
            binding.recyclerAnchorVideoList.post(() -> {
                if (adapter != null) {
                    adapter.requestFocus();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (playerViewModel != null) {
            playerViewModel.playingVideo.removeObservers(getViewLifecycleOwner());
        }
        if (videoSource != null) {
            videoSource.setOnReadyListener(null);
        }
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_COVER_UPDATE);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_COVER_UPDATE.equals(key)) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        super.onListenerChange(key, data);
    }

    @Override
    public void onClick(View v) {
        if (anchor == null) {
            return;
        }
        if (v.getId() == binding.btnFollow.getId()) {
            if (anchor.getFollow() == 1) {
                reqUnFollowAnchor(anchor);
            } else {
                reqFollowAnchor(anchor);
            }
        }
    }

    public void setAnchor(ModelAnchor.Item anchor) {
        this.anchor = anchor;
        updateView();
        if (videoSource != null && anchor != null) {
            videoSource.setAnchorId(anchor.getAnchor_id());
            videoSource.reqLiveList();
        }
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    private void reqFollowAnchor(ModelAnchor.Item item) {
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
                    SafeToast.showToast(R.string.follow_success, Toast.LENGTH_SHORT);
                    updateView();
                }
            }
        });
    }

    private void reqUnFollowAnchor(ModelAnchor.Item item) {
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
                    SafeToast.showToast(R.string.unfollow_success, Toast.LENGTH_SHORT);
                    updateView();
                }
            }
        });
    }

    private static class AnchorVideoDataSource extends VideoListDataSource {
        private String anchorId;

        public void setAnchorId(String anchorId) {
            this.anchorId = anchorId;
        }

        @Override
        public void reqLiveList() {
            TaskHelper.exec(new TaskHelper.Task() {
                ModelWrapper<ModelLive> model;
                @Override
                public void execute() throws Exception {

//                    List<ModelLive.Item> list = new ArrayList<>();
//                    for (int i = 0; i < 10; i++) {
//                        ModelLive.Item item = new ModelLive.Item();
//                        item.setId("11");
//                        item.setTitle("Video " + i);
//                        item.setBack_img("https://img.cbnlive.cn/web/uploads/image/store_1/bd3ecde03cc241c818fccccffeac9a3e3528809e.jpg");
//                        item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
//                        list.add(item);
//                    }
//                    live.setList(list);
                    model = UserMethod.getInstance().getAnchorInfo(anchorId, 1, 10);
                }

                @Override
                public void callback(Exception e) {
                    if (model != null && model.isSuccess() && model.getData() != null) {
                        updateLiveList(model.getData());
                    }
                }
            });
        }
    }
}
