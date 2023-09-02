package cn.cibn.kaibo.ui.settings;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.core.view.GravityCompat;

import com.tv.lib.frame.adapter.ListBindingAdapter;

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
        versionHistoryAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelVersion.Item>() {
            @Override
            public void onItemClick(ModelVersion.Item item) {
                binding.drawerVersionDetail.openDrawer(GravityCompat.END);
                binding.tvVersionDetail.setText(item.getContent());
            }
        });
    }

    @Override
    protected void initData() {
        List<ModelVersion.Item> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ModelVersion.Item item = new ModelVersion.Item();
            item.setVersionName("1.0.0");
            item.setUpdateTime("2023-12-23");
            item.setSize("12MB");
            item.setContent(i + "\n1.更人性化的操作界面\n" +
                    "界面全新设计，清新简洁，点划之问尽享愉悦的操作体验\n" +
                    "\n" +
                    "2.体验大有不同\n" +
                    "耳目一新的短视频，优惠商品、关注主播、热门点赞，你要找的商品，都在CIBN\n" +
                    "\n" +
                    "3.新增蝰蛇音效\n" +
                    "极致丽音，由蝰蛇(VIPER)专业打造的智能均衡环绕音。更有多种预设音效，让同短视频也有不同的味道");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (binding.drawerVersionDetail.isDrawerOpen(GravityCompat.END)) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK) {
                binding.drawerVersionDetail.closeDrawer(GravityCompat.END);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
