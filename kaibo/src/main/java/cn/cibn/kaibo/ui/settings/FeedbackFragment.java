package cn.cibn.kaibo.ui.settings;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.Utils;
import com.tv.lib.core.utils.device.DeviceHelper;
import com.tv.lib.core.utils.ui.SafeToast;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentFeedbackBinding;
import cn.cibn.kaibo.databinding.ItemFeedbackBinding;
import cn.cibn.kaibo.model.ModelOption;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.SettingMethod;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class FeedbackFragment extends KbBaseFragment<FragmentFeedbackBinding> {

    private FeedbackAdapter feedbackAdapter;

    private String deviceId;
    private String deviceModel;
    private String utdid;
    private String version;

    public static FeedbackFragment createInstance() {
        return new FeedbackFragment();
    }

    @Override
    protected FragmentFeedbackBinding createBinding(LayoutInflater inflater) {
        return FragmentFeedbackBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        deviceId = DeviceHelper.getDeviceId(ObjectStore.getContext());
        deviceModel = Build.MODEL;
        utdid = DeviceHelper.getUtdid(ObjectStore.getContext());
        version = Utils.getVersionName(mContext);
        StringBuilder buf = new StringBuilder();
        buf.append("设备UUID：").append(deviceId);
        buf.append("\n设备型号：").append(deviceModel);
        buf.append("\nUTDID：").append(utdid);
        buf.append("\n应用版本：").append(version);
        binding.tvDeviceInfo.setText(buf.toString());

        feedbackAdapter = new FeedbackAdapter();
        binding.recyclerFeedback.setAdapter(feedbackAdapter);
        binding.recyclerFeedback.setNumColumns(4);
        feedbackAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelOption>() {
            @Override
            public void onItemClick(ModelOption item) {
                reqSubmitFeedback(item);
            }
        });
    }

    @Override
    protected void initData() {
        reqOptionList();
//        List<String> list = new ArrayList<>();
//        list.add("播放时一直加载中");
//        list.add("播放时黑屏");
//        list.add("无法播放提示错误代码");
//        list.add("闪退、无响应");
//        list.add("视频直播一直卡");
//        list.add("购物无法扫码");
//        list.add("无法播放提示错误代码");
//        list.add("无法播放提示错误代码");
//        list.add("无法播放提示错误代码");
//        list.add("无法播放提示错误代码");
//        feedbackAdapter.submitList(list);
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

    private void reqOptionList() {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<List<ModelOption>> model;

            @Override
            public void execute() throws Exception {
                model = SettingMethod.getInstance().reqOptionList();
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    feedbackAdapter.submitList(model.getData());
                }
            }
        });
    }

    private void reqSubmitFeedback(ModelOption option) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<String> model;

            @Override
            public void execute() throws Exception {
                model = SettingMethod.getInstance().reqFeedback(option.getId(), deviceId, deviceModel, utdid, version);
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess()) {
                    SafeToast.showToast("提交成功", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private static class FeedbackAdapter extends ListBindingAdapter<ModelOption, ItemFeedbackBinding> {
        private View lastSelectedView = null;

        public FeedbackAdapter() {
            super(new OptionDiffCallback());
        }

        @Override
        public ItemFeedbackBinding createBinding(ViewGroup parent) {
            return ItemFeedbackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        @Override
        public void onBindViewHolder(ModelOption data, ItemFeedbackBinding binding, int position) {
            binding.getRoot().setBackgroundResource(ConfigModel.getInstance().isGrayMode() ? R.drawable.shape_14_1affffff_selector_gray : R.drawable.shape_4_1affffff_selector);
            binding.tvFeedback.setText(data.getTitle());
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

    private static class OptionDiffCallback extends DiffUtil.ItemCallback<ModelOption> {

        @Override
        public boolean areItemsTheSame(@NonNull ModelOption oldItem, @NonNull ModelOption newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ModelOption oldItem, @NonNull ModelOption newItem) {
            return oldItem.equals(newItem);
        }
    }
}
