package cn.cibn.kaibo.ui;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.leanback.widget.BaseGridView;

import com.tv.lib.core.Logger;
import com.tv.lib.core.utils.ui.SafeToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.VideoListAdapter;
import cn.cibn.kaibo.databinding.FragmentMenuBinding;
import cn.cibn.kaibo.model.ModelLive;

public class MenuFragment extends KbBaseFragment<FragmentMenuBinding> {
    private static final String TAG = "MenuFragment";

    private MenuFragment subFragment;
    private VideoListAdapter adapter;

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
        binding.btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafeToast.showToast("推荐", Toast.LENGTH_SHORT);
                if (grade == 1) {
                    binding.menuDrawer.openDrawer(GravityCompat.START);
                }
            }
        });
        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafeToast.showToast("关注", Toast.LENGTH_SHORT);
            }
        });
        binding.btnMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafeToast.showToast("我的", Toast.LENGTH_SHORT);
            }
        });

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

    public void requestFocus() {
        binding.btnRecommend.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnRecommend.requestFocus();
            }
        }, 10);

        List<ModelLive.Item> itemList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ModelLive.Item item = new ModelLive.Item();
            item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
            item.setName("Video");
            item.setId(String.valueOf(i));
            item.setBack_img("https://img.cbnlive.cn/web/uploads/image/store_1/503630a36565cb688fc94bb7380fd1fe9fb99cf5.jpg");
            itemList.add(item);
        }
        adapter.submitList(itemList);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.menuDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.menuDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (binding.menuDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.menuDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }
        return false;
    }
}
