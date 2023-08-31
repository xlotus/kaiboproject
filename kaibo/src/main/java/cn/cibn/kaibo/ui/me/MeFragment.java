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
        subBinding.btnMyFollow.setOnClickListener(this);
        subBinding.btnMyHistory.setOnClickListener(this);

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
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        super.requestFocus();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == subBinding.btnMyFollow.getId()) {
            Bundle bundle = new Bundle();
            bundle.putString("page", "follow");
            getParentFragmentManager().setFragmentResult("menu", bundle);
        } else if (id == subBinding.btnMyHistory.getId()) {
            Bundle bundle = new Bundle();
            bundle.putString("page", "history");
            getParentFragmentManager().setFragmentResult("menu", bundle);
        }
    }
}
