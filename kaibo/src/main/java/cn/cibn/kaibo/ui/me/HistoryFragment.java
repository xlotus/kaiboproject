package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.VideoGridAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentMeHistoryBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.UserMethod;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.ui.video.VideoListDataSource;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class HistoryFragment extends KbBaseFragment<FragmentMeHistoryBinding> {
    private static final String TAG = "HistoryFragment";
    private VideoGridAdapter adapter;
    private HistoryVideoDataSource videoSource;

    private PlayerViewModel playerViewModel;

    public static HistoryFragment createInstance() {
        return new HistoryFragment();
    }

    @Override
    protected FragmentMeHistoryBinding createBinding(LayoutInflater inflater) {
        return FragmentMeHistoryBinding.inflate(inflater);
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
        Logger.d(TAG, "initView");
        adapter = new VideoGridAdapter();
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
        binding.recyclerMeHistoryList.setNumColumns(3);
        binding.recyclerMeHistoryList.setAdapter(adapter);

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
        videoSource = new HistoryVideoDataSource();
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
        videoSource.reqLiveList();
    }

    @Override
    protected void updateView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(TAG, "onDestroyView");
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

    private static class HistoryVideoDataSource extends VideoListDataSource {

        @Override
        public void reqLiveList() {
            TaskHelper.exec(new TaskHelper.Task() {
                ModelWrapper<ModelLive> model;

                @Override
                public void execute() throws Exception {
                    model = UserMethod.getInstance().getHistoryRecord(1, 10);
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
