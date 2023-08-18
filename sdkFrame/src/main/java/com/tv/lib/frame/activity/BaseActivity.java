package com.tv.lib.frame.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.tv.lib.core.utils.Utils;
import com.tv.lib.core.utils.permission.PermissionsUtils;
import com.tv.lib.frame.loadingdialog.LoadingManager;
import com.tv.lib.utils.StatusBarUtil;

public abstract class BaseActivity extends FragmentActivity implements ILoading, PermissionsUtils.IPermissionRequestListener {

    private LoadingManager loadingManager;
    protected boolean mHasSetStatusColor = false;

    protected PermissionsUtils.PermissionRequestCallback mPermissionCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.setAdaptationRequestedOrientation(this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        loadingManager = new LoadingManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if (!mHasSetStatusColor) {
//            setStatusBar();
            mHasSetStatusColor = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.notifyPermissionsChange(permissions, grantResults, mPermissionCallback);
    }

    @Override
    public void setPermissionRequestListener(PermissionsUtils.PermissionRequestCallback callback) {
        mPermissionCallback = callback;
    }

    protected void setStatusBar() {
//        setStatusBar(Color.WHITE);
    }

    protected final void setStatusBar(@ColorInt int color) {
        StatusBarUtil.setColorNoTranslucent(this, color);
        StatusBarUtil.setLightMode(this);
    }

    @Override
    public void showLoading() {
        if (loadingManager != null) {
            loadingManager.show();
        }
    }

    @Override
    public void showLoading(String text) {
        if (loadingManager != null) {
            loadingManager.show(text);
        }
    }

    @Override
    public void hideLoading() {
        if (loadingManager != null) {
            loadingManager.hide(null);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

}
