package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import cn.cibn.kaibo.databinding.FragmentMeBinding;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class MeFragment extends BaseStackFragment<FragmentMeBinding> {

    @Override
    protected FragmentMeBinding createSubBinding(LayoutInflater inflater) {
        return FragmentMeBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnMyFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("page", "follow");
                getParentFragmentManager().setFragmentResult("menu", bundle);
            }
        });
        subBinding.btnMyFollow.requestFocus();
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
}
