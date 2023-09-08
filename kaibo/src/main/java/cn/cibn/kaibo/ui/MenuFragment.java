package cn.cibn.kaibo.ui;

import android.graphics.Color;
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
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentMenuBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.player.VideoType;

public class MenuFragment extends KbBaseFragment<FragmentMenuBinding> implements View.OnClickListener {
    private static final String TAG = "MenuFragment";

    private VideoListAdapter adapter;

    private View selectedView;

    public static MenuFragment createInstance() {
        return new MenuFragment();
    }

    @Override
    protected FragmentMenuBinding createBinding(LayoutInflater inflater) {
        return FragmentMenuBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        binding.menuDrawer.setScrimColor(Color.TRANSPARENT);
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
            binding.btnSearch.setBackgroundResource(R.drawable.bg_menu_item_search_selector_gray);
            binding.btnRecommend.setBackgroundResource(R.drawable.bg_menu_item_selector_gray);
            binding.btnFollow.setBackgroundResource(R.drawable.bg_menu_item_selector_gray);
            binding.btnMe.setBackgroundResource(R.drawable.bg_menu_item_selector_gray);
            binding.btnSearch.setMenuNameColor(R.color.menu_item_selector_gray);
            binding.btnRecommend.setMenuNameColor(R.color.menu_item_selector_gray);
            binding.btnFollow.setMenuNameColor(R.color.menu_item_selector_gray);
            binding.btnMe.setMenuNameColor(R.color.menu_item_selector_gray);
            binding.btnRecommend.setMenuIcon(R.drawable.menu_recommend_n);
            binding.btnFollow.setMenuIcon(R.drawable.menu_follow_n);
            binding.btnMe.setMenuIcon(R.drawable.menu_me_n);
        } else {
            binding.btnSearch.setBackgroundResource(R.drawable.bg_menu_item_search_selector);
            binding.btnRecommend.setBackgroundResource(R.drawable.bg_menu_item_selector);
            binding.btnFollow.setBackgroundResource(R.drawable.bg_menu_item_selector);
            binding.btnMe.setBackgroundResource(R.drawable.bg_menu_item_selector);
            binding.btnSearch.setMenuNameColor(R.color.menu_item_selector);
            binding.btnRecommend.setMenuNameColor(R.color.menu_item_selector);
            binding.btnFollow.setMenuNameColor(R.color.menu_item_selector);
            binding.btnMe.setMenuNameColor(R.color.menu_item_selector);
            binding.btnRecommend.setMenuIcon(R.drawable.menu_recommend_selector);
            binding.btnFollow.setMenuIcon(R.drawable.menu_follow_selector);
            binding.btnMe.setMenuIcon(R.drawable.menu_me_selector);
        }
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
            selectedView.setSelected(false);
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
            item.setType(VideoType.SHORT.getValue());
            item.setId("300");
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
            openPage("search");
        } else if (id == binding.btnRecommend.getId()) {
            if (grade == 1) {
                binding.btnRecommend.setSelected(true);
                selectedView = binding.btnRecommend;
            }
        } else if (id == binding.btnFollow.getId()) {
            binding.btnFollow.setSelected(true);
            selectedView = binding.btnFollow;
            openPage("homeFollow");
        } else if (id == binding.btnMe.getId()) {
            binding.btnMe.setSelected(true);
            selectedView = binding.btnMe;
            openPage("me");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.menuDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.menuDrawer.closeDrawer(GravityCompat.START);
                if (selectedView != null) {
                    selectedView.setSelected(false);
                    selectedView = null;
                }
                requestFocus();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (binding.menuDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.menuDrawer.closeDrawer(GravityCompat.START);
                if (selectedView != null) {
                    selectedView.setSelected(false);
                    selectedView = null;
                }
                requestFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_COVER_UPDATE);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_COVER_UPDATE.equals(key)) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        super.onListenerChange(key, data);
    }

    private void openPage(String page) {
        if (getActivity() != null) {
            Bundle result = new Bundle();
            result.putString("page", page);
            getActivity().getSupportFragmentManager().setFragmentResult("menu", result);
        }
    }
}
