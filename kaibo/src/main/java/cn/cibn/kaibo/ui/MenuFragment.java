package cn.cibn.kaibo.ui;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;

import com.tv.lib.core.Logger;
import com.tv.lib.core.utils.ui.SafeToast;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.databinding.FragmentMenuBinding;

public class MenuFragment extends KbBaseFragment<FragmentMenuBinding> {
    private static final String TAG = "MenuFragment";

    private MenuFragment subFragment;

    @Override
    protected FragmentMenuBinding createBinding(LayoutInflater inflater) {
        return FragmentMenuBinding.inflate(inflater);
    }

    @Override
    protected void updateView() {

    }

    @Override
    protected void initView() {
        if (grade == 1) {
            subFragment = new MenuFragment();
            subFragment.grade = 2;
            getChildFragmentManager().beginTransaction().replace(R.id.sub_menu_container, subFragment).commit();
        }
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
//        binding.getRoot().requestFocus();
//        binding.menuRoot.requestFocus();
//        binding.btnRecommend.requestFocus();
//        binding.btnRecommend.requestFocusFromTouch();
//        binding.btnRecommend.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                binding.btnRecommend.requestFocus();
//            }
//        }, 1000);
        requestFocus();
    }

    @Override
    protected void initData() {

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
