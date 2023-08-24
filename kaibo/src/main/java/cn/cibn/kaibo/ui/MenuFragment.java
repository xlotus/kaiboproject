package cn.cibn.kaibo.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.leanback.widget.BaseGridView;

import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.utils.ui.SafeToast;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.VideoListAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentMenuBinding;
import cn.cibn.kaibo.model.ModelLive;

public class MenuFragment extends KbBaseFragment<FragmentMenuBinding> implements View.OnClickListener {
    private static final String TAG = "MenuFragment";

    private MenuFragment subFragment;
    private VideoListAdapter adapter;

    private View selectedView;

    @Override
    protected FragmentMenuBinding createBinding(LayoutInflater inflater) {
        return FragmentMenuBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        if (grade == 1) {
            subFragment = new MenuFragment();
            subFragment.grade = 2;
            getChildFragmentManager().beginTransaction().replace(R.id.sub_menu_container, subFragment).commit();
        }
        adapter = new VideoListAdapter();
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelLive.Item>() {
            @Override
            public void onItemClick(ModelLive.Item item) {
                ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_LIVE_ITEM_CLICKED, item);
            }
        });
        binding.recyclerVideoList.setAdapter(adapter);
        binding.recyclerVideoList.setOnKeyInterceptListener(new BaseGridView.OnKeyInterceptListener() {
            @Override
            public boolean onInterceptKeyEvent(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && binding.recyclerVideoList.getSelectedPosition() == 0) {
                        binding.btnMe.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });
        adapter.submitList(Collections.emptyList());
        binding.btnSearch.setOnClickListener(this);
        binding.btnRecommend.setOnClickListener(this);
        binding.btnFollow.setOnClickListener(this);
        binding.btnMe.setOnClickListener(this);

        requestFocus();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
        binding.btnRecommend.requestFocus();
    }

    @Override
    public void requestFocus() {
        if (selectedView != null) {
            selectedView.requestFocus();
        } else {
            binding.btnRecommend.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.btnRecommend.requestFocus();
                }
            }, 10);
        }

        List<ModelLive.Item> itemList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ModelLive.Item item = new ModelLive.Item();
            item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
            item.setTitle("VideoVideoVideoVideoVideoVideoVideoVideoVideoVideo");
            item.setId(String.valueOf(i));
            item.setBack_img("https://img.cbnlive.cn/web/uploads/image/store_1/503630a36565cb688fc94bb7380fd1fe9fb99cf5.jpg");
            itemList.add(item);
        }
        adapter.submitList(itemList);
    }

    @Override
    public void onClick(View v) {
        if (selectedView != null) {
            selectedView.setSelected(false);
        }
        int id = v.getId();
        if (id == binding.btnSearch.getId()) {
            binding.btnSearch.setSelected(true);
            SafeToast.showToast("搜索", Toast.LENGTH_SHORT);
            Bundle result = new Bundle();
            result.putString("page", "search");
            selectedView = binding.btnSearch;
            getParentFragmentManager().setFragmentResult("menu", result);
        } else if (id == binding.btnRecommend.getId()) {
            SafeToast.showToast("推荐", Toast.LENGTH_SHORT);
            if (grade == 1) {
                binding.menuDrawer.openDrawer(GravityCompat.START);
                binding.btnRecommend.setSelected(true);
                selectedView = binding.btnRecommend;
                subFragment.requestFocus();
            }
        } else if (id == binding.btnFollow.getId()) {
            SafeToast.showToast("关注", Toast.LENGTH_SHORT);
        } else if (id == binding.btnMe.getId()) {
            binding.btnMe.setSelected(true);
            SafeToast.showToast("我的", Toast.LENGTH_SHORT);
            Bundle result = new Bundle();
            result.putString("page", "me");
            selectedView = binding.btnMe;
            getParentFragmentManager().setFragmentResult("menu", result);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.menuDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.menuDrawer.closeDrawer(GravityCompat.START);
                requestFocus();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (binding.menuDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.menuDrawer.closeDrawer(GravityCompat.START);
                requestFocus();
                return true;
            }
        }
        return false;
    }
}
