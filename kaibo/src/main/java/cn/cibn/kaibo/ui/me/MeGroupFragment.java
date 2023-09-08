package cn.cibn.kaibo.ui.me;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentResultListener;

import java.util.ArrayList;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentMeBinding;
import cn.cibn.kaibo.databinding.FragmentMeGroupBinding;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class MeGroupFragment extends BaseStackFragment<FragmentMeGroupBinding> implements View.OnClickListener {

    private FollowFragment followFragment;
    private HistoryFragment historyFragment;

    private AnchorFragment anchorFragment;

    private int page = 0;

    public static MeGroupFragment createInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        MeGroupFragment fragment = new MeGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentMeGroupBinding createSubBinding(LayoutInflater inflater) {
        return FragmentMeGroupBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnGoHome.setOnClickListener(this);
        subBinding.btnPageFollow.setOnClickListener(this);
        subBinding.btnPageHistory.setOnClickListener(this);
        subBinding.drawerMeGroup.setScrimColor(Color.TRANSPARENT);
        followFragment = FollowFragment.createInstance();
        historyFragment = HistoryFragment.createInstance();
        anchorFragment = AnchorFragment.createInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.me_group_container, anchorFragment).commit();

        getChildFragmentManager().setFragmentResultListener("meGroup", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String type = result.getString("type");
                if ("anchor".equals(type)) {
                    ModelAnchor.Item item = (ModelAnchor.Item) result.getSerializable("data");
                    subBinding.drawerMeGroup.openDrawer(GravityCompat.END);
                    anchorFragment.setAnchor(item);
                    anchorFragment.requestFocus();
                    anchorFragment.setOpen(true);
                } else if ("history".equals(type)) {
                    ModelLive.Item item = (ModelLive.Item) result.getSerializable("data");
                }
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("page", 0);
        }
        updateView();
    }

    @Override
    protected void updateView() {
        if (subBinding == null) {
            return;
        }
        if (page == 0) {
            subBinding.btnPageFollow.requestFocus();
            showFollow();
        } else {
            subBinding.btnPageHistory.requestFocus();
            showHistory();
        }
        if (ConfigModel.getInstance().isGrayMode()) {
            subBinding.btnGoHome.setBackgroundResource(R.drawable.bg_menu_item_search_selector_gray);
            subBinding.btnPageFollow.setBackgroundResource(R.drawable.bg_recyclerview_item_gray);
            subBinding.btnPageHistory.setBackgroundResource(R.drawable.bg_recyclerview_item_gray);
        } else {
            subBinding.btnGoHome.setBackgroundResource(R.drawable.bg_menu_item_search_selector);
            subBinding.btnPageFollow.setBackgroundResource(R.drawable.bg_recyclerview_item);
            subBinding.btnPageHistory.setBackgroundResource(R.drawable.bg_recyclerview_item);
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
        } else if (id == subBinding.btnPageFollow.getId()) {
            showFollow();
        } else if (id == subBinding.btnPageHistory.getId()) {
            showHistory();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (subBinding.drawerMeGroup.isDrawerOpen(GravityCompat.END)) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK) {
                subBinding.drawerMeGroup.closeDrawer(GravityCompat.END);
                followFragment.requestFocus();
                anchorFragment.setOpen(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().clearFragmentResultListener("meGroup");
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_REQUEST_SUB_PLAY);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_REQUEST_SUB_PLAY.equals(key)) {
            if (subBinding != null) {
                subBinding.drawerMeGroup.closeDrawer(GravityCompat.END);
            }
            return;
        }
        super.onListenerChange(key, data);
    }

    private void showFollow() {
        getChildFragmentManager().beginTransaction().replace(R.id.me_group_content, followFragment).commit();
    }

    private void showHistory() {
        getChildFragmentManager().beginTransaction().replace(R.id.me_group_content, historyFragment).commit();
    }
}
