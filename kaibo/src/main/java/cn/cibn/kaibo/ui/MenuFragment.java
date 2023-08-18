package cn.cibn.kaibo.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tv.lib.core.Logger;
import com.tv.lib.core.utils.ui.SafeToast;

import cn.cibn.kaibo.databinding.FragmentMenuBinding;

public class MenuFragment extends KbBaseFragment<FragmentMenuBinding> {
    private static final String TAG = "MenuFragment";

    @Override
    protected FragmentMenuBinding createBinding(LayoutInflater inflater) {
        return FragmentMenuBinding.inflate(inflater);
    }

    @Override
    protected void updateView() {

    }

    @Override
    protected void initView() {
        binding.btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafeToast.showToast("推荐", Toast.LENGTH_SHORT);
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
}
