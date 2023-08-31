package cn.cibn.kaibo.ui.orders;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import cn.cibn.kaibo.databinding.FragmentOrdersHomeBinding;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class OrdersHomeFragment extends BaseStackFragment<FragmentOrdersHomeBinding> implements View.OnClickListener {

    private int page = 0;

    public static OrdersHomeFragment createInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        OrdersHomeFragment fragment = new OrdersHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentOrdersHomeBinding createSubBinding(LayoutInflater inflater) {
        return FragmentOrdersHomeBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnGoHome.setOnClickListener(this);


        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("page", 0);
        }
        updateView();
    }

    @Override
    protected void updateView() {
        if (page == 0) {
            subBinding.btnPageNeedPay.requestFocus();
            showNeedPay();
        } else {
            subBinding.btnPageNeedSend.requestFocus();
            showNeedSend();
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
        } else if (id == subBinding.btnPageNeedPay.getId()) {
            showNeedPay();
        } else if (id == subBinding.btnPageNeedSend.getId()) {
            showNeedSend();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().clearFragmentResultListener("meGroup");
    }

    private void showNeedPay() {
//        getChildFragmentManager().beginTransaction().replace(R.id.me_group_content, followFragment).commit();
    }

    private void showNeedSend() {
//        getChildFragmentManager().beginTransaction().replace(R.id.me_group_content, historyFragment).commit();
    }
}
