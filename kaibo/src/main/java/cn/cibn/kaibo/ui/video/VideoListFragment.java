package cn.cibn.kaibo.ui.video;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.leanback.widget.BaseGridView;

import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.Collections;

import cn.cibn.kaibo.DataManger;
import cn.cibn.kaibo.adapter.VideoListAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentVideoListBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class VideoListFragment extends KbBaseFragment<FragmentVideoListBinding> {
    private static final String TAG = "VideoListFragment";

    private VideoListAdapter adapter;

    public static VideoListFragment createInstance() {
        Logger.d(TAG, "createInstance");
        return new VideoListFragment();
    }

    @Override
    protected FragmentVideoListBinding createBinding(LayoutInflater inflater) {
        return FragmentVideoListBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        Logger.d(TAG, "initView");
        binding.recyclerVideoList.setOnKeyInterceptListener(new BaseGridView.OnKeyInterceptListener() {
            @Override
            public boolean onInterceptKeyEvent(KeyEvent keyEvent) {
                Logger.d(TAG, "onInterceptKeyEvent = " + keyEvent);
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && binding.recyclerVideoList.getSelectedPosition() == adapter.getItemCount() - 1) {
                        binding.recyclerVideoList.setSelectedPosition(0);
                        requestFocus(0);
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && binding.recyclerVideoList.getSelectedPosition() == 0) {
                        binding.recyclerVideoList.setSelectedPosition(adapter.getItemCount() - 1);
                        requestFocus(adapter.getItemCount() - 1);
                        return true;
                    }
                }
                return false;
            }
        });

        adapter = new VideoListAdapter();
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelLive.Item>() {
            @Override
            public void onItemClick(ModelLive.Item item) {
                ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_LIVE_ITEM_CLICKED, item);
            }
        });
        binding.recyclerVideoList.setAdapter(adapter);
        adapter.submitList(Collections.emptyList());
    }

    @Override
    protected void initData() {
        ArrayList<ModelLive.Item> dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ModelLive.Item live = new ModelLive.Item();
            live.setTitle("live " + i);
            live.setName("anchor" + i);
            dataList.add(live);
        }
        adapter.submitList(dataList);
    }

    @Override
    protected void updateView() {
        Logger.d(TAG, "updateView");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_LIVE_LIST_UPDATE);
        keys.add(ChangedKeys.CHANGED_CURRENT_LIVE_UPDATE);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_LIVE_LIST_UPDATE.equals(key)) {
            Logger.d(TAG, "onListenerChange, CHANGED_LIVE_LIST_UPDATE");
            adapter.submitList(DataManger.getInstance().getLiveList());
            adapter.notifyDataSetChanged();
            return;
        }
        if (ChangedKeys.CHANGED_CURRENT_LIVE_UPDATE.equals(key)) {
            binding.recyclerVideoList.scrollToPosition(DataManger.getInstance().getCurLivePosition());
            adapter.notifyDataSetChanged();
            return;
        }
        super.onListenerChange(key, data);
    }

    public void requestFocus() {
        int position = DataManger.getInstance().getCurLivePosition();
        requestFocus(position);
    }

    private void requestFocus(int position) {
        binding.recyclerVideoList.scrollToPosition(position);
        binding.recyclerVideoList.post(new Runnable() {
            @Override
            public void run() {
                adapter.setLastSelectedView(binding.recyclerVideoList.getLayoutManager().findViewByPosition(position));
                if (adapter != null) {
                    adapter.requestFocus();
                }
            }
        });
    }
}
