package cn.cibn.kaibo.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.databinding.FragmentFeedbackBinding;
import cn.cibn.kaibo.databinding.ItemFeedbackBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.utils.StringDiffCallback;

public class FeedbackFragment extends KbBaseFragment<FragmentFeedbackBinding> {

    private FeedbackAdapter feedbackAdapter;

    public static FeedbackFragment createInstance() {
        return new FeedbackFragment();
    }

    @Override
    protected FragmentFeedbackBinding createBinding(LayoutInflater inflater) {
        return FragmentFeedbackBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        feedbackAdapter = new FeedbackAdapter();
        binding.recyclerFeedback.setAdapter(feedbackAdapter);
        binding.recyclerFeedback.setNumColumns(4);
    }

    @Override
    protected void initData() {
        List<String> list = new ArrayList<>();
        list.add("播放时一直加载中");
        list.add("播放时黑屏");
        list.add("无法播放提示错误代码");
        list.add("闪退、无响应");
        list.add("视频直播一直卡");
        list.add("购物无法扫码");
        list.add("无法播放提示错误代码");
        list.add("无法播放提示错误代码");
        list.add("无法播放提示错误代码");
        list.add("无法播放提示错误代码");
        feedbackAdapter.submitList(list);
    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        if (binding != null) {
            binding.recyclerFeedback.post(new Runnable() {
                @Override
                public void run() {
                    feedbackAdapter.requestFocus();
                }
            });
        }
    }

    private static class FeedbackAdapter extends ListBindingAdapter<String, ItemFeedbackBinding> {
        private View lastSelectedView = null;

        public FeedbackAdapter() {
            super(new StringDiffCallback());
        }

        @Override
        public ItemFeedbackBinding createBinding(ViewGroup parent) {
            return ItemFeedbackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        @Override
        public void onBindViewHolder(String data, ItemFeedbackBinding binding, int position) {
            binding.tvFeedback.setText(data);
            if (position == 0) {
                lastSelectedView = binding.getRoot();
            }
        }

        public void requestFocus() {
            if (lastSelectedView != null) {
                lastSelectedView.requestFocus();
            }
        }
    }

}
