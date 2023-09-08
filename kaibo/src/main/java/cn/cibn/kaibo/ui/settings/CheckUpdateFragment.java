package cn.cibn.kaibo.ui.settings;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.GravityCompat;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentCheckUpdateBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class CheckUpdateFragment extends KbBaseFragment<FragmentCheckUpdateBinding> implements View.OnClickListener {

    private VersionHistoryFragment versionHistoryFragment;

    public static CheckUpdateFragment createInstance() {
        return new CheckUpdateFragment();
    }

    @Override
    protected FragmentCheckUpdateBinding createBinding(LayoutInflater inflater) {
        return FragmentCheckUpdateBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        binding.btnUpdateNow.setOnClickListener(this);
        binding.btnHistoryVersions.setOnClickListener(this);
        binding.btnClearCache.setOnClickListener(this);
        binding.drawerVersionHistory.setScrimColor(Color.TRANSPARENT);

        versionHistoryFragment = VersionHistoryFragment.createInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.version_history_container, versionHistoryFragment).commit();
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
        if (ConfigModel.getInstance().isGrayMode()) {
            binding.ivLauncher.setImageResource(R.drawable.ic_launcher_210_gray);
            binding.btnUpdateNow.setBackgroundResource(R.drawable.shape_22_33ffffff_selector_gray);
            binding.btnHistoryVersions.setBackgroundResource(R.drawable.shape_22_33ffffff_selector_gray);
            binding.btnClearCache.setBackgroundResource(R.drawable.shape_22_33ffffff_selector_gray);
        } else {
            binding.ivLauncher.setImageResource(R.drawable.ic_launcher_210);
            binding.btnUpdateNow.setBackgroundResource(R.drawable.shape_22_33ffffff_selector);
            binding.btnHistoryVersions.setBackgroundResource(R.drawable.shape_22_33ffffff_selector);
            binding.btnClearCache.setBackgroundResource(R.drawable.shape_22_33ffffff_selector);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnUpdateNow.getId()) {

        } else if (id == binding.btnHistoryVersions.getId()) {
            binding.drawerVersionHistory.openDrawer(GravityCompat.END);
            versionHistoryFragment.requestFocus();
        } else if (id == binding.btnClearCache.getId()) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (binding.drawerVersionHistory.isDrawerOpen(GravityCompat.END)) {
            if (versionHistoryFragment.onKeyDown(keyCode, event)) {
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK) {
                binding.drawerVersionHistory.closeDrawer(GravityCompat.END);
                binding.btnHistoryVersions.requestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
