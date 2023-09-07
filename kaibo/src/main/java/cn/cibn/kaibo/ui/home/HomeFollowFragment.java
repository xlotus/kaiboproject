package cn.cibn.kaibo.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentResultListener;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.adapter.HomeAnchorAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentHomeFollowBinding;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.UserMethod;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.me.AnchorFragment;
import cn.cibn.kaibo.ui.widget.LoginView;

public class HomeFollowFragment extends BaseStackFragment<FragmentHomeFollowBinding> {
    private static final String TAG = "HomeFollowFragment";

    private static final int PAGE_FIRST = 1;

    private HomeAnchorAdapter adapter;

    private AnchorFragment anchorFragment;

    private LoginView loginView;

    private List<ModelAnchor.Item> followList = new ArrayList<>();
    private int total = 0;
    private int curPage;
    private int loadingPage = -1;

    public static HomeFollowFragment createInstance() {
        return new HomeFollowFragment();
    }

    @Override
    protected FragmentHomeFollowBinding createSubBinding(LayoutInflater inflater) {
        return FragmentHomeFollowBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.tvFollowListTitle.setText(getString(R.string.follow_list_title, total));
        subBinding.drawerHomeFollow.setScrimColor(Color.TRANSPARENT);
        adapter = new HomeAnchorAdapter();
        subBinding.recyclerFollowAnchorList.setNumColumns(2);
        subBinding.recyclerFollowAnchorList.setAdapter(adapter);
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelAnchor.Item>() {
            @Override
            public void onItemClick(ModelAnchor.Item item) {
                subBinding.drawerHomeFollow.openDrawer(GravityCompat.START);
                anchorFragment.setAnchor(item);
                anchorFragment.requestFocus();
                anchorFragment.setOpen(true);
            }
        });
        subBinding.recyclerFollowAnchorList.addOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);

        anchorFragment = AnchorFragment.createInstance();
        getChildFragmentManager().beginTransaction().replace(subBinding.homeFollowContent.getId(), anchorFragment).commit();
        updateView();
    }

    @Override
    protected void updateView() {
        if (UserManager.getInstance().isLogin()) {
            subBinding.layoutFollowHasLogin.setVisibility(View.VISIBLE);
            subBinding.stubFollowLoginView.setVisibility(View.GONE);
            subBinding.layoutFollowHasLogin.requestFocus();
        } else {
            if (loginView == null) {
                loginView = (LoginView) subBinding.stubFollowLoginView.inflate();
            }
            subBinding.layoutFollowHasLogin.setVisibility(View.GONE);
            subBinding.stubFollowLoginView.setVisibility(View.VISIBLE);
            if (loginView != null) {
                loginView.login();
            }
        }
    }

    @Override
    protected void initData() {
        reqFollowList(PAGE_FIRST);
    }

    @Override
    public void requestFocus() {
        if (subBinding != null) {
            subBinding.recyclerFollowAnchorList.post(new Runnable() {
                @Override
                public void run() {
                    adapter.requestFocus();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (subBinding.drawerHomeFollow.isDrawerOpen(GravityCompat.START)) {
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                subBinding.drawerHomeFollow.closeDrawer(GravityCompat.START);
                requestFocus();
                anchorFragment.setOpen(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subBinding != null) {
            subBinding.recyclerFollowAnchorList.removeOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
        }
        if (adapter != null) {
            adapter.setOnItemClickListener(null);
        }
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_REQUEST_SUB_PLAY);
        keys.add(ChangedKeys.CHANGED_LOGIN_SUCCESS);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_REQUEST_SUB_PLAY.equals(key)) {
            if (subBinding != null) {
                subBinding.drawerHomeFollow.closeDrawer(GravityCompat.START);
            }
            return;
        }
        if (ChangedKeys.CHANGED_LOGIN_SUCCESS.equals(key)) {
            updateView();
            return;
        }
        super.onListenerChange(key, data);
    }

    private void reqFollowList(int page) {
        if (page == loadingPage) {
            return;
        }
        loadingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelAnchor> model;

            @Override
            public void execute() throws Exception {
                model = UserMethod.getInstance().getFollowList(page, 10);
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    curPage = loadingPage;
                    total = model.getData().getRow_count();
                    if (loadingPage == PAGE_FIRST) {
                        followList.clear();
                        subBinding.tvFollowListTitle.setText(getString(R.string.follow_list_title, total));
                    }
                    if (model.getData().getList() != null) {
                        followList.addAll(model.getData().getList());
                    }
                    adapter.submitList(followList);
                    requestFocus();
                }
            }
        });
    }

    private OnChildViewHolderSelectedListener onChildViewHolderSelectedListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
            int count = adapter.getItemCount();
            if (count - position < 6 && count < total) {
                reqFollowList(curPage + 1);
            }
        }
    };
}
