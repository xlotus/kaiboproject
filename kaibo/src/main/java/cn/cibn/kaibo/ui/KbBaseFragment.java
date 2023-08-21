package cn.cibn.kaibo.ui;

import android.view.KeyEvent;

import androidx.viewbinding.ViewBinding;

import com.tv.lib.frame.fragment.BaseFragment;

import java.util.ArrayList;

import cn.cibn.kaibo.change.ChangedKeys;

public abstract class KbBaseFragment<T extends ViewBinding> extends BaseFragment<T> {

    protected int grade = 1;

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {

        keys.add(ChangedKeys.CHANGED_GRAY_MODE);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_GRAY_MODE.equals(key)) {
            updateView();
            return;
        }
        super.onListenerChange(key, data);
    }

    public void requestFocus() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    protected abstract void updateView();
}
