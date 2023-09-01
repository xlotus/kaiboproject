package cn.cibn.kaibo.ui.settings;

import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.VersionHistoryAdapter;
import cn.cibn.kaibo.databinding.FragmentVersionHistoryBinding;
import cn.cibn.kaibo.model.ModelVersion;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class VersionHistoryFragment extends KbBaseFragment<FragmentVersionHistoryBinding> {

    private VersionHistoryAdapter versionHistoryAdapter;

    public static VersionHistoryFragment createInstance() {
        return new VersionHistoryFragment();
    }

    @Override
    protected FragmentVersionHistoryBinding createBinding(LayoutInflater inflater) {
        return FragmentVersionHistoryBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        versionHistoryAdapter = new VersionHistoryAdapter();
        binding.recyclerVersionHistory.setAdapter(versionHistoryAdapter);
    }

    @Override
    protected void initData() {
        List<ModelVersion.Item> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ModelVersion.Item item = new ModelVersion.Item();
            item.setVersionName("1.0.0");
            item.setUpdateTime("2023-12-23");
            list.add(item);
        }
        versionHistoryAdapter.submitList(list);
    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        if (binding != null) {
            binding.recyclerVersionHistory.post(new Runnable() {
                @Override
                public void run() {
                    versionHistoryAdapter.requestFocus();
                }
            });
        }
    }
}
