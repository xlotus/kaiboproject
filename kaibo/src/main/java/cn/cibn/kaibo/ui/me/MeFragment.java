package cn.cibn.kaibo.ui.me;

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentMeBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelUser;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.widget.LoginView;

public class MeFragment extends BaseStackFragment<FragmentMeBinding> implements View.OnClickListener {

    private static final String TAG = "MeFragment";

    private LoginView loginView;

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
            subBinding.layoutUserContent.setVisibility(View.VISIBLE);
            subBinding.stubLoginView.setVisibility(View.GONE);
            subBinding.btnMyFollow.requestFocus();
            ModelUser user = UserManager.getInstance().getUserInfo();
            if (user != null) {
                subBinding.tvAnchorName.setText("@" + user.getNickname());
                subBinding.tvAnchorId.setText("用户ID:" + user.getUser_key());
                ImageLoadHelper.loadCircleImage(subBinding.ivUserHead, user.getAvatar_url(), ConfigModel.getInstance().isGrayMode());
                if (user.getOrder_count().getStatus_0() > 0) {
                    subBinding.tvCountNeedPay.setText(String.valueOf(user.getOrder_count().getStatus_0()));
                    subBinding.tvCountNeedPay.setVisibility(View.VISIBLE);
                } else {
                    subBinding.tvCountNeedPay.setVisibility(View.GONE);
                }
                if (user.getOrder_count().getStatus_1() > 0) {
                    subBinding.tvCountNeedSend.setText(String.valueOf(user.getOrder_count().getStatus_1()));
                    subBinding.tvCountNeedSend.setVisibility(View.VISIBLE);
                } else {
                    subBinding.tvCountNeedSend.setVisibility(View.GONE);
                }
                if (user.getOrder_count().getStatus_2() > 0) {
                    subBinding.tvCountNeedReceive.setText(String.valueOf(user.getOrder_count().getStatus_2()));
                    subBinding.tvCountNeedReceive.setVisibility(View.VISIBLE);
                } else {
                    subBinding.tvCountNeedReceive.setVisibility(View.GONE);
                }
                if (user.getOrder_count().getStatus_3() > 0) {
                    subBinding.tvCountFinished.setText(String.valueOf(user.getOrder_count().getStatus_3()));
                    subBinding.tvCountFinished.setVisibility(View.VISIBLE);
                } else {
                    subBinding.tvCountFinished.setVisibility(View.GONE);
                }
                if (user.getOrder_count().getStatus_5() > 0) {
                    subBinding.tvCountService.setText(String.valueOf(user.getOrder_count().getStatus_5()));
                    subBinding.tvCountService.setVisibility(View.VISIBLE);
                } else {
                    subBinding.tvCountService.setVisibility(View.GONE);
                }

            }
        } else {
            subBinding.layoutUserContent.setVisibility(View.GONE);
            if (loginView == null) {
                loginView = (LoginView) subBinding.stubLoginView.inflate();
            }
            subBinding.stubLoginView.setVisibility(View.VISIBLE);
            if (loginView != null) {
                loginView.login();
            }
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

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_LOGIN_SUCCESS);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_LOGIN_SUCCESS.equals(key)) {
            updateView();
            return;
        }
        super.onListenerChange(key, data);
    }
}
