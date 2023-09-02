package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.databinding.FragmentMeBinding;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.utils.LoginHelper;

public class MeFragment extends BaseStackFragment<FragmentMeBinding> implements View.OnClickListener {

    @Override
    protected FragmentMeBinding createSubBinding(LayoutInflater inflater) {
        return FragmentMeBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnLogout.setOnClickListener(this);
        subBinding.btnMyFollow.setOnClickListener(this);
        subBinding.btnMyHistory.setOnClickListener(this);
        subBinding.layoutSettings.setOnClickListener(this);

        subBinding.layoutOrderNeedPay.setOnClickListener(this);
        subBinding.layoutOrderNeedSend.setOnClickListener(this);
        subBinding.layoutOrderNeedReceive.setOnClickListener(this);
        subBinding.layoutOrderFinished.setOnClickListener(this);
        subBinding.layoutOrderService.setOnClickListener(this);

        updateView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {
        if (UserManager.getInstance().isLogin()) {
            subBinding.groupLoginYes.setVisibility(View.VISIBLE);
            subBinding.groupLoginNo.setVisibility(View.GONE);
            subBinding.btnMyFollow.requestFocus();
        } else {
            subBinding.groupLoginYes.setVisibility(View.GONE);
            subBinding.groupLoginNo.setVisibility(View.VISIBLE);
            subBinding.ivLoginQrcode.requestFocus();
            LoginHelper.login(new OAuthListener() {
                @Override
                public void onAuthGotQrcode(String s, byte[] bytes) {

                }

                @Override
                public void onQrcodeScanned() {

                }

                @Override
                public void onAuthFinish(OAuthErrCode oAuthErrCode, String s) {

                }
            });
        }
    }

    @Override
    public void requestFocus() {
        super.requestFocus();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == subBinding.btnMyFollow.getId()) {
            openPage("meGroup", 0);
        } else if (id == subBinding.btnMyHistory.getId()) {
            openPage("meGroup", 1);
        } else if (id == subBinding.btnLogout.getId()) {
            UserManager.getInstance().clear();
            updateView();
        } else if (id == subBinding.layoutSettings.getId()) {
            openPage("settings", 0);
        } else if (id == subBinding.layoutOrderNeedPay.getId()) {
            openPage("orders", 0);
        } else if (id == subBinding.layoutOrderNeedSend.getId()) {
            openPage("orders", 1);
        } else if (id == subBinding.layoutOrderNeedReceive.getId()) {
            openPage("orders", 2);
        } else if (id == subBinding.layoutOrderFinished.getId()) {
            openPage("orders", 3);
        } else if (id == subBinding.layoutOrderService.getId()) {
            openPage("orders", 4);
        }
    }
}
