package com.tv.lib.frame.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.change.ChangedListener;
import com.tv.lib.frame.activity.BaseActivity;
import com.tv.lib.frame.activity.ILoading;

import java.util.ArrayList;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment implements ChangedListener {
    protected Context mContext;
    protected T binding;

    private final ArrayList<String> keys = new ArrayList<>();
    protected ILoading mLoading;

    protected Fragment mCurFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            requireActivity().getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event.getTargetState() == Lifecycle.State.CREATED) {
                        onActivityCreated();
                        requireActivity().getLifecycle().removeObserver(this);
                    }
                }
            });
        } catch (Exception e) {
            Logger.d("BaseFragment", e.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChangedListenerKey(keys);
        registerChangedListener();
        if (getActivity() instanceof BaseActivity) {
            mLoading = (ILoading) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = createBinding(inflater);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initData();
    }

    protected void onActivityCreated() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterChangedListener();
    }

    protected abstract T createBinding(LayoutInflater inflater);

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 添加需要监听的回调key
     **/
    protected void addChangedListenerKey(ArrayList<String> keys) {

    }

    private void registerChangedListener() {
        for (String key : keys) {
            ChangeListenerManager.getInstance().registerChangedListener(key, this);
        }
    }

    private void unregisterChangedListener() {
        for (String key : keys) {
            ChangeListenerManager.getInstance().unregisterChangedListener(key, this);
        }
    }

    @Override
    public void onListenerChange(String key, Object data) {
        Logger.d("BaseFragment", "==========key=%s", key);
    }

    /**
     * 切换fragment
     **/
    protected void switchTab(@IdRes int layoutId, Fragment from, Fragment to, String tag) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (from == null) {
            if (fm.findFragmentByTag(tag) == null && !to.isAdded()) {
                transaction.add(layoutId, to, tag);
            }
        } else {
            if (fm.findFragmentByTag(tag) == null && !to.isAdded()) {
                transaction.hide(from).add(layoutId, to, tag);
            } else {
                transaction.hide(from).show(to);
            }
        }
        mCurFragment = to;
        transaction.commitAllowingStateLoss();
    }

}
