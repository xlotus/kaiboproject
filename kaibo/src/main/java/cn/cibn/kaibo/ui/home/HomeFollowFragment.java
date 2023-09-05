package cn.cibn.kaibo.ui.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentResultListener;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.adapter.HomeAnchorAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentHomeFollowBinding;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.me.AnchorFragment;
import cn.cibn.kaibo.ui.widget.LoginView;

public class HomeFollowFragment extends BaseStackFragment<FragmentHomeFollowBinding> {

    private HomeAnchorAdapter adapter;

    private AnchorFragment anchorFragment;

    private LoginView loginView;

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
        adapter = new HomeAnchorAdapter();
        subBinding.recyclerFollowAnchorList.setNumColumns(2);
        subBinding.recyclerFollowAnchorList.setAdapter(adapter);
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelAnchor.Item>() {
            @Override
            public void onItemClick(ModelAnchor.Item item) {
                subBinding.drawerHomeFollow.openDrawer(GravityCompat.START);
                anchorFragment.setAnchor(item);
                anchorFragment.requestFocus();
            }
        });

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
        List<ModelAnchor.Item> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ModelAnchor.Item item = new ModelAnchor.Item();
            item.setTitle("anchor" + i);
            item.setCover_img("https://img.cbnlive.cn/web/uploads/image/store_1/bd3ecde03cc241c818fccccffeac9a3e3528809e.jpg");
            list.add(item);
        }
        adapter.submitList(list);
        requestFocus();
    }

    @Override
    public void requestFocus() {
        if (subBinding != null) {
            subBinding.recyclerFollowAnchorList.post(new Runnable() {
                @Override
                public void run() {
//                    subBinding.recyclerFollowAnchorList.requestChildFocus(null, null);
//                    subBinding.recyclerFollowAnchorList.getChildAt(0).requestFocus();
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
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
}
