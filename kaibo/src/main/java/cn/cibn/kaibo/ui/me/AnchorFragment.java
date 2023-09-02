package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.VideoListAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentAnchorBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.ui.video.VideoListDataSource;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class AnchorFragment extends KbBaseFragment<FragmentAnchorBinding> {

    private VideoListAdapter adapter;
    private AnchorVideoDataSource videoSource;

    private PlayerViewModel playerViewModel;

    private ModelAnchor.Item anchor;

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
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                requestFocus();
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
            binding.tvAnchorName.setText("@" + anchor.getTitle());
            binding.tvAnchorId.setText("用户ID:" + anchor.getId());
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
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
    }

    public void setAnchor(ModelAnchor.Item anchor) {
        this.anchor = anchor;
        updateView();
        if (videoSource != null) {
            videoSource.reqLiveList();
        }
    }

    private static class AnchorVideoDataSource extends VideoListDataSource {

        @Override
        public void reqLiveList() {
            ModelLive live = new ModelLive();
            TaskHelper.exec(new TaskHelper.Task() {
                @Override
                public void execute() throws Exception {

                    List<ModelLive.Item> list = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        ModelLive.Item item = new ModelLive.Item();
                        item.setId("11");
                        item.setTitle("Video " + i);
                        item.setBack_img("https://img.cbnlive.cn/web/uploads/image/store_1/bd3ecde03cc241c818fccccffeac9a3e3528809e.jpg");
                        item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
                        list.add(item);
                    }
                    live.setList(list);
                }

                @Override
                public void callback(Exception e) {
                    updateLiveList(live);
                }
            });
        }
    }
}
