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
    private FeedbackFragment feedbackFragment;
    private ProblemsFragment problemsFragment;
    private AboutFragment aboutFragment;
    private CheckNetworkFragment checkNetworkFragment;

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
        subBinding.btnPageCheckUpdate.setOnClickListener(this);
        subBinding.btnPageFeedBack.setOnClickListener(this);
        subBinding.btnPageProblems.setOnClickListener(this);
        subBinding.btnPageAbout.setOnClickListener(this);
        subBinding.btnPageCheckNetwork.setOnClickListener(this);

        checkUpdateFragment = CheckUpdateFragment.createInstance();
        feedbackFragment = FeedbackFragment.createInstance();
        problemsFragment = ProblemsFragment.createInstance();
        aboutFragment = AboutFragment.createInstance();
        checkNetworkFragment = CheckNetworkFragment.createInstance();


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
            showContent(checkUpdateFragment);
        } else {
            subBinding.btnPageFeedBack.requestFocus();
            showContent(feedbackFragment);
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
            showContent(checkUpdateFragment);
        } else if (id == subBinding.btnPageFeedBack.getId()) {
            showContent(feedbackFragment);
        } else if (id == subBinding.btnPageProblems.getId()) {
            showContent(problemsFragment);
        } else if (id == subBinding.btnPageAbout.getId()) {
            showContent(aboutFragment);
        } else if (id == subBinding.btnPageCheckNetwork.getId()) {
            showContent(checkNetworkFragment);
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
        getChildFragmentManager().beginTransaction().replace(subBinding.settingsContent.getId(), feedbackFragment).commit();
        contentFragment = feedbackFragment;
    }

    private void showContent(KbBaseFragment<?> contentFragment) {
        getChildFragmentManager().beginTransaction().replace(subBinding.settingsContent.getId(), contentFragment).commit();
        this.contentFragment = problemsFragment;
    }

    private void showAbout() {

    }

    private void showCheckNetwork() {

    }
}
