package cn.cibn.kaibo.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tv.lib.core.Logger;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.databinding.FragmentSearchBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.ui.widget.SearchKeyboard;

public class SearchFragment extends KbBaseFragment<FragmentSearchBinding> implements Handler.Callback {
    private static final String TAG = "SearchFragment";
    private static final int WHAT_SEARCH = 1;

    private Handler handler;

    private String inputValue = "";

    private SearchHistoryFragment historyFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
    }

    @Override
    protected FragmentSearchBinding createBinding(LayoutInflater inflater) {
        return FragmentSearchBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        binding.searchKeyboard.setOnSearchKeyListener(new SearchKeyboard.OnSearchKeyListener() {
            @Override
            public void onSearchKey(String key) {
                inputValue += key;
                binding.etSearch.setText(inputValue);
                binding.etSearch.setSelection(inputValue.length());
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(WHAT_SEARCH, 500);
                }
            }
        });
        showHistory();

        binding.layoutSearchMiddle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Logger.d(TAG, "onFocusChange " + hasFocus);
            }
        });
        binding.getRoot().getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Logger.d(TAG, "onGlobalFocusChanged " + oldFocus + " | " + newFocus);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_SEARCH) {
            reqSearchNames();
            return true;
        }
        return false;
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

    private void reqSearchNames() {

    }

    private void showHistory() {
        if (historyFragment == null) {
            historyFragment = new SearchHistoryFragment();
        }
        getChildFragmentManager().beginTransaction().replace(R.id.layout_search_middle, historyFragment).commit();
    }
}
