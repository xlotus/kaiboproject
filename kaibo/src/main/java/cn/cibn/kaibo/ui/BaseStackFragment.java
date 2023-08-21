package cn.cibn.kaibo.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.viewbinding.ViewBinding;

import cn.cibn.kaibo.databinding.FragmentBaseStatckBinding;

public abstract class BaseStackFragment<T extends ViewBinding> extends KbBaseFragment<FragmentBaseStatckBinding> {

    protected T subBinding;

    @Override
    protected final FragmentBaseStatckBinding createBinding(LayoutInflater inflater) {
        return FragmentBaseStatckBinding.inflate(inflater);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Bundle bundle = new Bundle();
            bundle.putString("page", "back");
            getParentFragmentManager().setFragmentResult("menu", bundle);
            return true;
        }
        return false;
    }

    @Override
    @CallSuper
    protected void initView() {
        subBinding = createSubBinding(LayoutInflater.from(mContext));
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        binding.getRoot().addView(subBinding.getRoot(), lp);
    }

    protected abstract T createSubBinding(LayoutInflater inflater);
}
