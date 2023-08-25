package cn.cibn.kaibo.ui.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.tv.lib.core.Logger;

import java.lang.ref.WeakReference;

import cn.cibn.kaibo.R;

public abstract class BaseDialog extends DialogFragment {

    private static final String TAG = "BaseDialog";

    private WeakReference<FragmentManager> managerReference;
    protected View parent;
    private DialogInterface.OnDismissListener dismissListener;

    public BaseDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = inflater.inflate(getLayoutResId(), container, false);
        initView(parent);
        initData();
        return parent;
    }

    protected abstract @LayoutRes int getLayoutResId();

    protected abstract void initView(View parent);

    protected abstract void initData();

    protected abstract String getShowTag();

    public void setFragmentManager(FragmentManager manager) {
        managerReference = new WeakReference<>(manager);
    }

    /**
     * 显示弹框
     **/
    public BaseDialog show() {
        FragmentManager manager = managerReference != null ? managerReference.get() : null;
        Logger.d(TAG, "showDialog manager=" + manager);
        if (manager != null) {
            try {
                show(manager, getShowTag());
                Logger.d(TAG, "showDialog success");
                return this;
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        Logger.d(TAG, "showDialog failed");
        return null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Logger.d(TAG, "dialog onDismiss");

        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    dismiss();
                }
                return false;
            }
        });
        d.getWindow().setGravity(Gravity.BOTTOM);
        return d;
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }
}
