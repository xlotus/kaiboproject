package cn.cibn.kaibo.ui.settings;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.MenuAdapter;
import cn.cibn.kaibo.databinding.FragmentSettingsBinding;
import cn.cibn.kaibo.model.MenuItem;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class SettingsFragment extends BaseStackFragment<FragmentSettingsBinding> implements View.OnClickListener {

    private CheckUpdateFragment checkUpdateFragment;
    private FeedbackFragment feedbackFragment;
    private ProblemsFragment problemsFragment;
    private AboutFragment aboutFragment;
    private CheckNetworkFragment checkNetworkFragment;

    private KbBaseFragment<?> contentFragment;

    private MenuAdapter menuAdapter;

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

        menuAdapter = new MenuAdapter();
        subBinding.recyclerSettingsMenu.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<MenuItem>() {
            @Override
            public void onItemClick(MenuItem item) {
                page = item.getId();
                updateView();
            }
        });

        checkUpdateFragment = CheckUpdateFragment.createInstance();
        feedbackFragment = FeedbackFragment.createInstance();
        problemsFragment = ProblemsFragment.createInstance();
        aboutFragment = AboutFragment.createInstance();
        checkNetworkFragment = CheckNetworkFragment.createInstance();


        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("page", 0);
        }
        subBinding.recyclerSettingsMenu.post(new Runnable() {
            @Override
            public void run() {
                if (subBinding.recyclerSettingsMenu.getChildCount() > page) {
                    subBinding.recyclerSettingsMenu.getChildAt(page).requestFocus();
                }
            }
        });
        updateView();
    }

    @Override
    protected void updateView() {
        if (page == 0) {
            showContent(checkUpdateFragment);
        } else if (page == 1){
            showContent(feedbackFragment);
        } else if (page == 2) {
            showContent(problemsFragment);
        } else if (page == 3) {
            showContent(aboutFragment);
        } else if (page == 4) {
            showContent(checkNetworkFragment);
        }
    }

    @Override
    protected void initData() {
        List<MenuItem> menus = new ArrayList<>();
        menus.add(new MenuItem(0, getString(R.string.check_update)));
        menus.add(new MenuItem(1, getString(R.string.feedback)));
        menus.add(new MenuItem(2, getString(R.string.problems)));
        menus.add(new MenuItem(3, getString(R.string.about_us)));
        menus.add(new MenuItem(4, getString(R.string.check_network)));
        menuAdapter.submitList(menus);
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
        this.contentFragment = contentFragment;
    }

    private void showAbout() {

    }

    private void showCheckNetwork() {

    }
}
