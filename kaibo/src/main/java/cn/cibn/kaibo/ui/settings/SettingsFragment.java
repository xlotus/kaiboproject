package cn.cibn.kaibo.ui.settings;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.databinding.FragmentSettingsBinding;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class SettingsFragment extends BaseStackFragment<FragmentSettingsBinding> implements View.OnClickListener {

    private CheckUpdateFragment checkUpdateFragment;

    private KbBaseFragment<?> contentFragment;

    private int page = 0;

    public static SettingsFragment createInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentSettingsBinding createSubBinding(LayoutInflater inflater) {
        return FragmentSettingsBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnGoHome.setOnClickListener(this);

        checkUpdateFragment = new CheckUpdateFragment();

        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("page", 0);
        }
        updateView();
    }

    @Override
    protected void updateView() {
        if (page == 0) {
            subBinding.btnPageCheckUpdate.requestFocus();
            showCheckUpdate();
        } else {
            subBinding.btnPageFeedBack.requestFocus();
            showFeedback();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void requestFocus() {
        super.requestFocus();
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == subBinding.btnGoHome.getId()) {
            goHome();
        } else if (id == subBinding.btnPageCheckUpdate.getId()) {
            showCheckUpdate();
        } else if (id == subBinding.btnPageFeedBack.getId()) {
            showFeedback();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (contentFragment != null && contentFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().clearFragmentResultListener("meGroup");
    }

    private void showCheckUpdate() {
        getChildFragmentManager().beginTransaction().replace(subBinding.settingsContent.getId(), checkUpdateFragment).commit();
        contentFragment = checkUpdateFragment;
    }

    private void showFeedback() {
//        getChildFragmentManager().beginTransaction().replace(R.id.me_group_content, historyFragment).commit();
    }
}
